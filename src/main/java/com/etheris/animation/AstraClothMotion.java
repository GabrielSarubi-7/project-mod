package com.etheris.animation;

import java.util.LinkedHashMap;
import java.util.Map;

/** Cosmetic, bounded joint dynamics; no Minecraft classes or changes to player movement. */
public final class AstraClothMotion {
    public record Sample(double time, double x, double y, double z, double bodyYaw,
                         double headYaw, double stride, double movement, boolean crouching,
                         boolean swimming, boolean airborne) {}

    /** Angles in radians. Pitch is positive towards the wearer's back. */
    public static final class Joint {
        private final Spring pitch = new Spring(), yaw = new Spring(), roll = new Spring();
        public double pitch() { return pitch.value; }
        public double yaw() { return yaw.value; }
        public double roll() { return roll.value; }
        private void step(double x, double y, double z, double frequency, double dt) {
            pitch.step(x, frequency, dt);
            yaw.step(y, frequency, dt);
            roll.step(z, frequency, dt);
        }
    }

    private static final class Spring {
        double value, velocity;
        void step(double target, double frequency, double dt) {
            target = clamp(target, -1.35, 1.35);
            // Semi-implicit integration, at most 1/120 s per step; slightly underdamped cloth.
            velocity += ((target - value) * frequency * frequency - 1.65 * frequency * velocity) * dt;
            value += velocity * dt;
            if (Math.abs(value) > 1.4) { value = clamp(value, -1.4, 1.4); velocity = 0; }
        }
    }

    private final Map<String, Joint> joints = new LinkedHashMap<>();
    private Sample previous;
    private double forward, sideways, vertical, bodyTurn, headTurn;

    public AstraClothMotion() {
        joints.put("cape_root", new Joint());
        for (String column : new String[]{"right", "center", "left"})
            for (int i = 0; i < 4; i++) joints.put("cape_" + column + "_" + i, new Joint());
        for (String panel : new String[]{"robe_right", "robe_left", "stole_front", "stole_back"})
            for (int i = 0; i < 3; i++) joints.put(segment(panel, i), new Joint());
        for (String accessory : new String[]{"sleeve_right", "sleeve_left", "talisman_right", "talisman_left"})
            joints.put(accessory, new Joint());
    }

    public Map<String, Joint> joints() { return java.util.Collections.unmodifiableMap(joints); }
    public Joint joint(String name) { return joints.get(name); }

    public void update(Sample sample) {
        if (previous == null) { reset(sample); return; }
        double dt = sample.time - previous.time;
        double dx = sample.x - previous.x, dy = sample.y - previous.y, dz = sample.z - previous.z;
        // Reappearing, changing worlds or teleporting must not whip cloth across the screen.
        if (dt < 0 || dt > .3 || dx * dx + dy * dy + dz * dz > 16) { reset(sample); return; }
        if (dt < 1e-6) return; // Repeated armor passes and pause do not advance the simulation.
        double angle = Math.toRadians(sample.bodyYaw);
        double targetForward = clamp((-dx * Math.sin(angle) + dz * Math.cos(angle)) / dt, -10, 10);
        double targetSideways = clamp((dx * Math.cos(angle) + dz * Math.sin(angle)) / dt, -10, 10);
        double targetVertical = clamp(dy / dt, -12, 12);
        double targetBodyTurn = clamp(Math.toRadians(wrapDegrees(sample.bodyYaw - previous.bodyYaw)) / dt, -6, 6);
        double targetHeadTurn = clamp(Math.toRadians(wrapDegrees(sample.headYaw - previous.headYaw)) / dt, -6, 6);
        int steps = (int)Math.ceil(dt * 120);
        double h = dt / steps;
        for (int i = 1; i <= steps; i++) {
            double blend = 1 - Math.exp(-10 * h);
            forward += (targetForward - forward) * blend;
            sideways += (targetSideways - sideways) * blend;
            vertical += (targetVertical - vertical) * blend;
            bodyTurn += (targetBodyTurn - bodyTurn) * blend;
            headTurn += (targetHeadTurn - headTurn) * blend;
            double mix = (double)i / steps;
            double time = previous.time + h * i;
            double stride = previous.stride + (sample.stride - previous.stride) * mix;
            double movement = clamp(previous.movement + (sample.movement - previous.movement) * mix, 0, 1);
            step(time, stride, movement, sample.crouching, sample.swimming, sample.airborne, h);
        }
        previous = sample;
    }

    private void reset(Sample sample) {
        previous = sample;
        forward = sideways = vertical = bodyTurn = headTurn = 0;
        for (Joint joint : joints.values()) for (Spring spring : new Spring[]{joint.pitch, joint.yaw, joint.roll}) {
            spring.value = 0; spring.velocity = 0;
        }
    }

    private void step(double time, double stride, double movement, boolean crouch,
                      boolean swimming, boolean airborne, double dt) {
        double speed = Math.hypot(forward, sideways);
        double breath = Math.sin(time * 1.65) * .012;
        double lift = clamp(.09 + Math.max(0, forward) * .10 + speed * .025
                - vertical * .018 + (airborne ? .07 : 0), .055, 1.02);
        if (swimming) lift = .18 + speed * .025;
        joints.get("cape_root").step(crouch ? -.18 : 0, 0, 0, 14, dt);
        // Segment angles are relative to the torso. The renderer converts them to local
        // bone rotations. Gravity pulls the tip down, with delayed follow-through.
        for (String column : new String[]{"right", "center", "left"}) {
            double side = column.equals("left") ? 1 : column.equals("right") ? -1 : 0;
            Joint parent = null;
            for (int i = 0; i < 4; i++) {
                double ripple = Math.sin(time * 3.6 - i * .85) * (.009 + Math.min(speed, 6) * .003);
                double pitch = parent == null ? lift + breath : parent.pitch() * .78 + lift * .10 + ripple;
                double yaw = -.055 * bodyTurn + sideways * .012;
                double roll = sideways * .016 + side * .007 * Math.sin(time * 2 - i * .6);
                Joint joint = joints.get("cape_" + column + "_" + i);
                joint.step(pitch, yaw, roll, 12 - i * 1.6, dt);
                parent = joint;
            }
        }
        for (String panel : new String[]{"robe_right", "robe_left", "stole_front", "stole_back"}) {
            boolean lateral = panel.startsWith("robe");
            double sign = panel.endsWith("right") ? 1 : -1;
            double stepSwing = Math.cos(stride) * movement;
            double pitch = lateral ? sign * stepSwing * .38 + forward * .016
                    : panel.endsWith("front") ? -.09 - Math.abs(stepSwing) * .46 - (crouch ? .26 : 0)
                    : .08 + Math.abs(stepSwing) * .35 + Math.max(0, forward) * .025;
            Joint parent = null;
            for (int i = 0; i < 3; i++) {
                double follow = parent == null ? pitch : parent.pitch() * .82 + pitch * .12;
                double roll = lateral ? sign * (.045 + movement * .045) : sideways * .018;
                Joint joint = joints.get(segment(panel, i));
                joint.step(follow + breath, -bodyTurn * .035, roll, 15 - i * 2.5, dt);
                parent = joint;
            }
        }
        for (String side : new String[]{"right", "left"}) {
            double sign = side.equals("right") ? 1 : -1;
            joints.get("sleeve_" + side).step(-sign * Math.cos(stride) * movement * .48 + breath,
                    -.035 * bodyTurn, sign * .04 + sideways * .02, 9, dt);
            joints.get("talisman_" + side).step(forward * .025 + sign * breath,
                    -.10 * headTurn, -headTurn * .095 + sideways * .022 + sign * breath, 8, dt);
        }
    }

    public static String segment(String name, int index) { return index == 0 ? name : name + "_" + index; }
    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
    private static double wrapDegrees(double v) { return v - Math.floor((v + 180) / 360) * 360; }
}
