# KNNClassifierGui
Project from CS 1410

The machine learning algorithm will classify a face for its expression - either "smiling", "neutral", or "surprised". 

In general, this is a difficult problem. Instead, the program will compare the shape of the mouth to decide the expression. While it is possible to get the mouth shape using computer vision techniques, we will simplify this one further step and use images where the height and width of the mouth is hand-measured. To allow for faces different distances from the camera, these measurements are made uniform (or "normalized") by dividing them by the width of the face. These measurements are encoded into the image file name. If you look at the faces directory in the project, you can see these files.

Hope you guys enjoyed it!
