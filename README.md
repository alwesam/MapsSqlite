ShareLoc
==========

ShareLoc is an Android application where you can enter your favorite spots on the map, upload it to a remote server from which  other users can retrieve to their phones.  Conversely, you can retrieve spots uploaded by other users in your group or in different groups.

Here is a basic rundown of how the app is to be used:

– When you launch the app, a login page appears prompting you to enter your login information. Alternatively, you can continue as a guest, in which case you’ll be able to enter locations in map as well as download markers already entered into the remote database.   However, you’ll not be able to upload your markers to the database.

– You can register and create an account, which will enable you to upload your markers to the remote database from which other users can retrieve.

Screenshot_2014-11-21-15-05-11                                 Screenshot_2014-11-21-14-57-37

– The ShareLoc app uses Google Maps API (version 2).  By default, and if GPS is enabled, the map activity will be zoomed in to wherever your phone is located.  Alternatively, it will zoom in to a location configured in Settings (default is set to Vancouver).  If the app fails to locate the phone GPS coordinates or the input location in Settings, it will zoom in to Vancouver (which is hard-coded).

Screenshot_2014-11-21-15-06-33                                Screenshot_2014-11-21-15-07-04

– When you tap on the map, a new activity launches with the tapped address and prompts you to save that point to the phone’s sqlite database.  Once you save, a new marker will be added.

Screenshot_2014-11-21-14-59-26                              Screenshot_2014-11-21-14-59-42

– You can upload/download markers from the MySQL database hosted by a remote server.

Screenshot_2014-11-21-14-58-48                              Screenshot_2014-11-21-18-25-29

Further planned improvements and features include

Creating an app icon
Having the option to login through Facebook or Google+ account
Removing redundant code and simplifying some existing code.
Improving memory usage.
Enhancing security for transferring data from app to server and vice versa (via SSL connection)

Downloading and Installing ShareLoc

A beta release to be made soon..
