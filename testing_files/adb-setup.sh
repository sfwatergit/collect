adb install -r ../collect_app/build/outputs/apk/collect_app-debug.apk
adb install Lumosity.apk # OK if this fails, but it's slow
# This is suggested approach for ODK Briefcase... hopefully this will work
# fine! https://opendatakit.org/use/briefcase/
adb push NatsuZemi.xml /sdcard/odk/forms
# Not sure we need this
adb shell mkdir /sdcard/odk/forms/NatsuZemi-media
# Sadly, there's still some additional entry somewhere that results in a broken
# form. Currently doing via manual interface.
# adb shell rm "/sdcard/odk/forms/Let s Start.xml"
# adb shell rmdir "/sdcard/odk/forms/Let s Start-media"

