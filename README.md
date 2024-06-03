# Face Morphing
Face morphing is a process that involves the gradual transformation of one face into another. This technique is widely used in various fields such as animation, film, photography, and social media.  It typically involves blending two or more facial images to create a smooth transition from one face to another. The process requires sophisticated algorithms to accurately detect and map facial features, ensuring a realistic and natural-looking morph.

The project consists of an app written in **Java Android**, using [**OpenCV**](https://opencv.org/) and the native [**Dlib**](http://dlib.net/) library.

**OpenCV** (Open Source Computer Vision Library) is an open-source computer vision and machine learning software library. It's widely used for various applications, including image and video Processing (providing tools for basic image processing operations, such as filtering, transformation, and analysis) and feature detection and matching, including algorithms for detecting and matching features in images, such as corners, edges, and contours. It also supports advanced machine learning algorithms for object detection, face recognition, and object tracking.

**Dlib** is a modern C++ toolkit that contains machine learning algorithms and tools for creating complex software in various domains, such as image processing, computer vision, and robotics. It is designed to be highly modular and efficient, allowing developers to build high-performance applications.

- **For the process to compile and embed Dlib in my Android Studio project**, I am reffering to this online guide: https://ashiqulislamshaon.medium.com/setting-up-dlib-in-your-android-projects-using-android-studio-for-windows-and-mac-linux-3cb29273fdd0

- **To include OpenCV in my Android Studio project**, I referred to this online guide (but any guide that brings the version of OpenCV you want is fine): https://medium.com/@sdranju/android-studio-step-by-step-guide-for-setting-up-opencv-sdk-4-9-on-android-740547f3260b

- Finally, **for the process and steps that lead to the morphing of the two faces**, I am referring to the official guide of LearnOpenCV by @BIGVision: https://learnopencv.com/face-morph-using-opencv-cpp-python/

The [guide](https://learnopencv.com/face-morph-using-opencv-cpp-python/) reports the general steps to get a correct morphing result, including the code written in C++/alternatively in Python.
In this project, I follow the theoretical steps to take two input images as an example, identify the facial key points and the correspondences in both, and follow the warping steps of the two images, to then merge them into a third.

## Usage
```
git clone https://github.com/ptrespidi/face-morphing-java-android.git
```

Once you have downloaded the project, you can run the app and test its functionality through the Test button on the homepage. Inside _face-morphing-java-android/app/src/main/res/drawable_ there are two images for testing (the same ones used in the [official guide](https://learnopencv.com/face-morph-using-opencv-cpp-python/)), namely _ted_cruz.jpg_ and _hillary_clinton.jpg_.

For the simplicity of the project, I did not focus on adding functionality to the app, or making it more aesthetically pleasing, but rather the aim is to show the technical functioning and the process that leads to the manipulation of the two images using algorithmic computer vision techniques. To test the app you can add some images in _/drawable_, and set them in the two **imageViews** in _/face-morphing-java-android/app/src/main/res/layout/testactivity.xml_.

The images of [Secretary Hillary Clinton](https://www.google.com/url?sa=i&url=https%3A%2F%2Fit.wikiquote.org%2Fwiki%2FHillary_Clinton&psig=AOvVaw02vmpCURCC3hRz8hBEBfv0&ust=1717495315355000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCMCI7uKWv4YDFQAAAAAdAAAAABAE) and [Senator Ted Cruz](https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.texastribune.org%2Fdirectory%2Fted-cruz%2F&psig=AOvVaw18neygGh8LPqCv0vHTwwz2&ust=1717495282177000&source=images&cd=vfe&opi=89978449&ved=0CBQQjhxqFwoTCNDcitmWv4YDFQAAAAAdAAAAABAE) are in the Public Domain.




