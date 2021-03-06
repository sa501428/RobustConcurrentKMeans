# RobustConcurrentKMeans

Robust implementation of Concurrent KMeans to handle missing values.

This code is adapted from a version of KMeans implemented by Thinklab. Changes made to the original code include:<br>
(1) switch from doubles to floats<br>
(2) skipping NaNs (missing data)<br>
(3) KMeans++ style initialization<br>
(4) KMedians implementation<br>

Because the original code is released under GPLv3, we are required to release this library under GPLv3 as well.

Original code (under GPLv3+)
https://github.com/ariesteam/thinklab/blob/master/plugins/org.integratedmodelling.thinklab.geospace/src/org/integratedmodelling/geospace/kmeans/ConcurrentKMeans.java
