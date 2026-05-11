# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Specifies the number of optimization passes to be performed. By default, a single pass is performed. Multiple passes may result in further improvements.
# If no improvements are found after an optimization pass, the optimization is ended. Only applicable when optimizing.
-optimizationpasses 5

# Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting.
-dontskipnonpubliclibraryclasses

# Specifies not to ignore package visible library class members (fields and methods).
-dontskipnonpubliclibraryclassmembers

# Specifies not to preverify the processed class files
-dontpreverify

# Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose