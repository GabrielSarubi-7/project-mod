package com.etheris.animation;

/** Dependency-free behavioral regression checks, run by Gradle check. */
public final class AstraClothMotionCheck {
    private static AstraClothMotion.Sample sample(double t, double z, double yaw, double movement) {
        return new AstraClothMotion.Sample(t, 0, 0, z, yaw, yaw, t * 8, movement, false, false, false);
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static double[] snapshot(AstraClothMotion motion) {
        return motion.joints().values().stream().flatMapToDouble(j ->
                java.util.stream.DoubleStream.of(j.pitch(), j.yaw(), j.roll())).toArray();
    }
    private static double difference(double[] a, double[] b) {
        double worst = 0;
        for (int i = 0; i < a.length; i++) worst = Math.max(worst, Math.abs(a[i] - b[i]));
        return worst;
    }
    private static AstraClothMotion running(int fps, double seconds) {
        AstraClothMotion motion = new AstraClothMotion();
        for (int i = 0; i <= (int)(seconds * fps); i++) {
            double t = (double)i / fps;
            motion.update(sample(t, 5.6 * t, 0, .9));
        }
        return motion;
    }

    public static void main(String[] args) {
        AstraClothMotion run = running(60, 2);
        require(run.joint("cape_center_0").pitch() > .55, "Running should lift the cape behind the body");
        require(run.joint("cape_center_3").pitch() < run.joint("cape_center_0").pitch() - .08,
                "Cape tip should drape rather than form one rigid plate");
        double deviation = Math.max(difference(snapshot(run), snapshot(running(30, 2))),
                difference(snapshot(run), snapshot(running(144, 2))));
        require(deviation < .025, "30/60/144 FPS response diverges: " + deviation);

        double before = run.joint("cape_center_3").pitch();
        run.update(sample(2 + 1.0 / 60, 11.2, 0, 0));
        require(Math.abs(run.joint("cape_center_3").pitch() - before) < .03,
                "Stopping must not snap the cape instantly to rest");
        for (int i = 2; i <= 240; i++) run.update(sample(2 + i / 60.0, 11.2, 0, 0));
        require(run.joint("cape_center_0").pitch() < .13 && run.joint("cape_center_3").pitch() < .13,
                "Cloth should settle after stopping");

        double[] paused = snapshot(run);
        for (int i = 0; i < 20; i++) run.update(sample(6, 11.2, 0, 0));
        require(difference(paused, snapshot(run)) == 0, "Pause or repeated render advanced the dynamics");
        run.update(sample(6.02, 1000, 0, 0));
        require(java.util.Arrays.stream(snapshot(run)).allMatch(v -> v == 0), "Teleport did not clear inertia");
        run.update(sample(20, 1000, 0, 0));
        require(java.util.Arrays.stream(snapshot(run)).allMatch(v -> v == 0), "Long absence did not reset");
        run.update(sample(1, 0, 0, 0));
        require(java.util.Arrays.stream(snapshot(run)).allMatch(v -> v == 0), "Clock rewind did not reset");

        AstraClothMotion wrapped = new AstraClothMotion(), continuous = new AstraClothMotion();
        wrapped.update(sample(0, 0, 179, 0)); continuous.update(sample(0, 0, 179, 0));
        wrapped.update(sample(.05, 0, -179, 0)); continuous.update(sample(.05, 0, 181, 0));
        require(difference(snapshot(wrapped), snapshot(continuous)) < 1e-10, "Yaw wrapping causes a false impulse");

        AstraClothMotion still = new AstraClothMotion(), turning = new AstraClothMotion();
        for (int i = 0; i <= 60; i++) {
            still.update(sample(i / 60.0, 0, 0, 0));
            turning.update(sample(i / 60.0, 0, i * 1.5, 0));
        }
        require(Math.abs(turning.joint("talisman_left").roll() - still.joint("talisman_left").roll()) > .08,
                "Talismans should react to head turns");
        require(Math.abs(still.joint("cape_center_0").yaw()) < .001, "A second wearer inherited turn inertia");
        AstraClothMotion stress = new AstraClothMotion();
        for (int i = 0; i < 2400; i++) {
            double t = i / 120.0;
            stress.update(new AstraClothMotion.Sample(t, Math.sin(t * 6) * 3, Math.sin(t * 4),
                    Math.cos(t * 6) * 3, t * 1600, -t * 1800, t * 16, 1,
                    i % 80 < 40, i % 300 > 200, i % 60 > 30));
            for (double angle : snapshot(stress)) require(Double.isFinite(angle) && Math.abs(angle) <= 1.4,
                    "Extreme motion produced an invalid or unbounded angle");
        }
        System.out.printf("PASS Astra cloth: run/drape/settle, FPS (max %.5f rad), pause, teleport, clock, yaw, wearer isolation, 2400 stress frames.%n", deviation);
    }
}
