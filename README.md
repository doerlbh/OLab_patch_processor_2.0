# OLab_patch_processor_2.0
A GUI to analysis the patchiness of V1 ocular dominance columns  

Youtube Demo: <https://youtu.be/dGP5D3Q4aGc>

By Baihan Lin, Olavarria Lab, May 2014

******************************************************************

About

This software is developed by Baihan Lin, Olavarria Lab, Department of Psychology, University of Washington. Main purpose of this software is to determine the patchness of ocular dominance column in V1 on ipsi and contra cortices. Other supplemental softwares includes Photoshop and ImageJ which serve as data sources. 

To use this software, you need to prepare the data generated from ImageJ's grayValue plot profile. Please save the data file ending with "data.txt" to be identified as target files. 

--"Patch Processor": click on which will lead you to enter paths of the folder with all the files (ending with "data.txt") to be analyzed. Press enter or click "analyze" will start the analysis. Result will be shown on the console window on the left. Two files (a txt file "result.pi" and an excel file "result.xls") will also be created in your folder with more concise summary of all the results analyzed. (Note: Smooth factor if not entered will be treated as default 2; Checkbox if checked will output excel-friendly data).

--"Smooth Factor Finder": click which will leads to an interface where the testing of proper smooth factor is performed on the first file in the given path. I coined smooth factor to indict the period of cycle in our chosen smoothing method--Moving Average. Thus smooth factor is the percentage of data moving towards the right, i.e. smooth factor  x means x% of the maximum data points per moving average. You can slide the sliding bar ranging from 0 to 100, and observe the dynamic changes in patch indexand the simulation of the graph itself on the canvas on the right.

--"ImageJ Component": Click which will leads to an interface where we can import our tiff file and draw our line on the interface to be analyzed all together. This includes a plugin of ImageJ into our software but will facilitate the entire process of analyzing patchness of ODC.

Version 1.0 and 2.0 was developed independently by Baihan Lin, Mentored by Dr. Robyn Laing and Prof. Jaime Olavarria. As stated above, more functions are stillunder development. All complaints and suggestions please be sent to sunnylin@uw.edu, and I am more than willing to improve the user experience and functionality of this software.
