# Face Morphing
Face morphing is a process that involves the gradual transformation of one face into another. This technique is widely used in various fields such as animation, film, photography, and social media.  It typically involves blending two or more facial images to create a smooth transition from one face to another. The process requires sophisticated algorithms to accurately detect and map facial features, ensuring a realistic and natural-looking morph.

The project consists of an app written in Java Android, using [**OpenCV**](https://opencv.org/) and the native [**Dlib**](http://dlib.net/) library.

**OpenCV** (Open Source Computer Vision Library) is an open-source computer vision and machine learning software library. It's widely used for various applications, including image and video Processing (providing tools for basic image processing operations, such as filtering, transformation, and analysis) and feature detection and matching, including algorithms for detecting and matching features in images, such as corners, edges, and contours. It also supports advanced machine learning algorithms for object detection, face recognition, and object tracking.

**Dlib** is a modern C++ toolkit that contains machine learning algorithms and tools for creating complex software in various domains, such as image processing, computer vision, and robotics. It is designed to be highly modular and efficient, allowing developers to build high-performance applications.

- **For the process to compile and embed Dlib in my Android Studio project**, I am reffering to this online guide: https://ashiqulislamshaon.medium.com/setting-up-dlib-in-your-android-projects-using-android-studio-for-windows-and-mac-linux-3cb29273fdd0

- **To include OpenCV in my Android Studio project**, I referred to this online guide (but any guide that brings the version of OpenCV you want is fine): https://medium.com/@sdranju/android-studio-step-by-step-guide-for-setting-up-opencv-sdk-4-9-on-android-740547f3260b

- Finally, **for the process and steps that lead to the morphing of the two faces**, I am referring to the official guide of LearnOpenCV by @BIGVision: https://learnopencv.com/face-morph-using-opencv-cpp-python/

The [guide](https://learnopencv.com/face-morph-using-opencv-cpp-python/) reports the general steps to get a correct morphing result, including the code written in C++/alternatively in Python.
In this project, I follow the theoretical steps to take two input images as an example, identify the facial key points and the correspondences in both, and follow the warping steps of the two images, to then merge them into a third.

## Usage
...
git clone https://github.com/ptrespidi/face-morphing-java-android.git
...






