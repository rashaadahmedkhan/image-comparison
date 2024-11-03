# image-comparison
 Apache Commons Imaging for open source image comparion in java

changes in the .gradle file:
dependencies {
    implementation 'org.apache.commons:commons-imaging:1.0-alpha3' // Check for the latest version
}

project structure
image-comparison/
├── build.gradle
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── Main.java
└── images/                      # Directory for storing images
    ├── image1.png              # First image for comparison
    └── image2.png              # Second image for comparison

percentage calculation: (different pixels/total pixels) * 100

to run it:
be in image-comparison directory
$ gradle build
$ gradle run