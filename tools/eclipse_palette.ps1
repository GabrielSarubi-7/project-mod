Add-Type -AssemblyName System.Drawing
$assetRoot = Join-Path $PSScriptRoot '../src/main/resources/assets/etheris/textures'
New-Item -ItemType Directory -Force -Path "$assetRoot/models/armor", "$assetRoot/item" | Out-Null
$colors = @('#161B30','#D9BC7B','#060911','#B8CEFF')
$atlas = [Drawing.Bitmap]::new(256,32)
for ($x=0; $x -lt 256; $x++) { for ($y=0; $y -lt 32; $y++) {
    $index=[int][Math]::Floor($x/64)
    $color=[Drawing.ColorTranslator]::FromHtml($colors[$index])
    if ($index -eq 0 -and ($x+$y)%7 -eq 0) { $color=[Drawing.ColorTranslator]::FromHtml('#242B46') }
    if ($index -eq 1 -and $y%6 -eq 0) { $color=[Drawing.ColorTranslator]::FromHtml('#F1DCA5') }
    $atlas.SetPixel($x,$y,$color)
} }
foreach ($layer in 1,2) { $atlas.Save("$assetRoot/models/armor/eclipse_layer_$layer.png",[Drawing.Imaging.ImageFormat]::Png) }
$atlas.Dispose()
# Small authored pixel silhouettes, not vanilla armor icons.
$icons=@{
    helmet=@('   G       G   ','   G   L   G   ','  GGGGGLGGGGG  ','  GDDDDDDDDDG  ','  GDKKKKKKKDG  ','  GDKLLKLLKDG  ','   DKKKKKKKD   ','   DDDDDDDDD   ','    GDDDDDG    ')
    chestplate=@(' GG       GGG  ','GDDGGGGGGGDDDG ','GDDDDGLGDDDDDG ',' DDDDGLGDDDDD  ',' DDDGLLLGDDDD  ',' DDDDKLKDDDDD  ','  DDDGLGDDDD   ','  DDDGLGDDDD   ','  GGGGGGGGGG   ','  DD DDD DD    ','  DD DDD DD    ')
    leggings=@('  GGGGGGGGGG   ','  DDDDDDDDDD   ','  DDDD  DDDD   ','  DDDD  DDDD   ','  DDDD  DDDD   ','  GLLG  GLLG   ','  DDDD  DDDD   ','  DDDD  DDDD   ')
    boots=@('  DDD   DDD    ','  DGD   DGD    ','  DGD   DGD    ',' DDDD  DDDD    ',' GGGG  GGGG    ')
}
foreach($part in $icons.Keys) {
    $icon=[Drawing.Bitmap]::new(16,16)
    $rows=$icons[$part]; $offset=[int][Math]::Floor((16-$rows.Count)/2)
    for($y=0;$y -lt $rows.Count;$y++) { for($x=0;$x -lt $rows[$y].Length;$x++) {
        $hex=switch($rows[$y][$x]) { 'G' {'#D9BC7B'} 'D' {'#252E4B'} 'K' {'#080B14'} 'L' {'#B8CEFF'} default {$null} }
        if($hex) { $icon.SetPixel($x,$y+$offset,[Drawing.ColorTranslator]::FromHtml($hex)) }
    } }
    $icon.Save("$assetRoot/item/eclipse_$part.png",[Drawing.Imaging.ImageFormat]::Png); $icon.Dispose()
}
