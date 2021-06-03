# Brother_Mini 
mini _Hacaktthon 7 # Pick Charity

Organization: Feeding SanDiego[https://feedingsandiego.org/]
Problem statement:1
Volunteer Name Tag printing:At distribution site with lots of volunteers to serve the packages needs name tags to differentiate from others all call out them so, NAME TAG is highly essential.Also real time data entry to log the checkin time and volunteer vehicle info are maintained in sheet which are to be filled out by each volunteer.
Manual entry by each volunteer on few sheets is a tedious process and name tags which are to ordered in bulk and maintained at social site is difficult.
![WhatsApp Image 2021-06-02 at 6 38 17 PM](https://user-images.githubusercontent.com/14889105/120573028-cb38a380-c3d1-11eb-8c11-821129b25fa9.jpeg)

Solution:1
A single window application by on site supervising volunteer to enter details of volunteers which simultaneously logs data to a google sheet which can log records of volunteer data and downloadable, and prints the Name tag on spot with standard organizations templet

![IMG_5138](https://user-images.githubusercontent.com/14889105/120573070-dbe91980-c3d1-11eb-94c0-16fca438437d.jpg)

Problem statement 2: With lots of volunteers woring at random shifts they need inventory management for food packed in boxes to wearhouse.Currently the content, expiry date and other details are written on boxes by packing volunteers which later the wearhouse volunteers have to enter into inventory management.
![IMG_20210601_162027](https://user-images.githubusercontent.com/14889105/120574722-b578ad80-c3d4-11eb-8b84-0d367f4b6c83.jpg)
![IMG_20210601_162024](https://user-images.githubusercontent.com/14889105/120574788-c9241400-c3d4-11eb-9317-581157f82bfa.jpg)

Solution 2:The packingvolunteers enter the details of content,etc into a simple ata logging app which uploads the details into a google spreadsheet which can be integrated to inventory management tool and prints out lable with all the info.
![WhatsApp Image 2021-06-02 at 6 59 02 PM](https://user-images.githubusercontent.com/14889105/120574839-dd681100-c3d4-11eb-9e9d-49b84a67fdaa.jpeg)
![Screenshot_20210602-201223](https://user-images.githubusercontent.com/14889105/120581912-751f2c80-c3e0-11eb-84e3-f1cad2ab9819.png)

How I Did it
1.Created a simple Form, ot the data.
2.Google Script to post data to the spreadsheet via url
3. Posted the form data from android app to google sheet 
4. Integreated RJ4250 printer with the android app
5. Saved the templet image on app installin internal storage which is the templet
6. Addded text on templet and On savedata is posted and tag/inventory is printed simultaneously.

