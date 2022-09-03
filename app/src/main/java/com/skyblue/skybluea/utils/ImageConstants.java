package com.skyblue.skybluea.utils;

public class ImageConstants {
    public static final String POST_PICTURE = "1";
    public static final String Temp = "2";
    public static final String PROFILE_PICTURE_UPLOAD = "3";
    public static final String COVER_PICTURE_UPLOAD = "4";

    public static final String PROFILE_IMAGE_VIEW = "11";
    public static final String PROFILE_IMAGE_VIEW_TITLE = "Profile picture";

    public static final String COVER_IMAGE_VIEW = "22";
    public static final String COVER_IMAGE_TITLE = "Cover picture";

    // Image chooser
     /*
            Intent putExtra : id ( contains )
            -> Update profile picture indicates by : id

               -> id = 1 ( Post picture )
               -> id = 2 ( Update profile picture - Registration initial stage)
               -> id = 3 ( Update Profile picture )
               -> id = 4 ( Update cover picture - Through account activity)
         */

    // Image delete

      /*

               Intent putExtra : image_r_id ( contains )
               -> Remove profile picture and Cover picture indicates by : image_r_id

                 -> image_r_id = 11 ( Profile picture )
                 -> image_r_id = 22 ( Cover picture )

         */


    /* private Constructor to avoid instanciating this class */
    private ImageConstants() {}
}
