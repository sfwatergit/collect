adb install -r ../collect_app/build/outputs/apk/collect_app-debug.apk
adb install Lumosity.apk # OK if this fails
# This is suggested approach for ODK Briefcase... hopefully this will work
# fine! https://opendatakit.org/use/briefcase/
adb push NatsuZemi-media /sdcard/odk/forms
adb push NatsuZemi.xml /sdcard/odk/forms
