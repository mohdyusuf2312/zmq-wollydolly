package zmq.com.photoquiz.utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;

import zmq.com.photoquiz.R;

/**
 * Created by zmq181 on 4/2/19.
 */

public class Utility {

    public static final String[][] ABC = {{"A","B","C","D","E","F","G","H","I","J"},
                                         {"K","L","M","N","O","P","Q","R","S"},
                                         {"T","U","V","W","X","Y","Z"}};


    public static final int[] images = {R.drawable.aditi,
            R.drawable.aishwarya,
            R.drawable.amirata,
            R.drawable.amisha,
            R.drawable.anuska,
            R.drawable.asin,
            R.drawable.bipasha,
            R.drawable.deepal,
            R.drawable.deepika,
            R.drawable.diya,
            R.drawable.esha,
            R.drawable.genelia,
            R.drawable.jacqueline,
            R.drawable.kangana,
            R.drawable.kareena,
            R.drawable.lara,
            R.drawable.malika,
            R.drawable.manisha,
            R.drawable.minissha,
            R.drawable.nargis,
            R.drawable.neha,
            R.drawable.prachi,
            R.drawable.priya,
            R.drawable.shruti,
            R.drawable.soha,
            R.drawable.sonakshi,
            R.drawable.sonam,
            R.drawable.vidya,
            R.drawable.zarin,
            R.drawable.katrina};

    public static final String[][] question = {
            {"winner of talent-hunt show$Cinestars Ki Khoj?","Rajjo in 'Mausam'?","Saira Rashid in ,'Ladies vs Ricky Bahl'?","Aditi Sharma"},
            {"Miss World of 1994?","Got  Padma Shri In 2009?","New paro in 'Devdas'?","Aishwarya Rai"},
            {"Acting debut with Ab Ke Baras?","Star Screen Award Best$Actress for Vivah?","'Ishq Vishk' girl?","Amrita Rao"},
            {"Kin with Ashmit?","Granddaughter of Barrister$Rajni Patel?","Acting debut in 'Kaho Naa, Pyaar Hai'?","Ameesha Patel"},
            {"Acted in 'Band Baaja Baaraat'?","Debut with 'Rab Ne$Bana Di Jodi'?","Starred in 'Badmaash Company'?","Anushka Sharma"},
            {"Debut in Ghajini?","Starred in House Full 2?","Won Best Female Debut Award$for Ghajini?","Asin"},
            {"won Godrej Cinthol Supermodel$Contest in 1996?","Acting Debut with 'Ajnabee'?","Sex symbol in 'Jism'?","Bipasha Basu"},
            {"First album Baby Doll?","In remixes Kabhi Aar$Kabhi Paar?","Acting debut in controversial$'Kalyug'?","Deepal Shaw"},
            {"Daughter of badminton player?","Double role in$'Chandni Chowk To China'?","Girl in 'Om Shanti Om'?","Deepika Padukone"},
            {"Debut with 'Rehnaa Hai$Terre Dil Mein'?","Won the Miss Asia Pacific$title in 2000?","Second runner up at Femina$Miss India 2000?","Dia Mirza"},
            {"Debut in Koi Mere Dil$Se Poochhe?","Daughter of  Hema Malini?","Daughter of Dharm Paaji?","Esha Deol"},
            {"Wife of Ritesh Deshmukh?","Debut with 'Tujhe Meri Kasam'?","Hosted the television show$Big Switch?","Genelia DSouza"},
            {"Won Miss Sri Lanka Universe$in 2006?","Debut with movie 'Aladin'?","Special appearance in song$of 'Housefull'?","Jacqueline Fernandez"},
            {"Starred in movie 'Tanu Weds Manu'?","Debut in 'Gangster'?","Won Filmfare Best Female Debut$Award 2006?","Kangna Ranaut"},
            {"Informally referred to as Bebo?","Starred in Jab We Met?","Sister of Karisma?","Kareena Kapoor"},
            {"Debut with the film 'Andaaz'?","Won Miss Intercontinental$in 1997?","Married to Mahesh Bhupathi?","Lara Dutta"},
            {"First movie 'Jeena Sirf$Merre Liye'?","With Jackie Chan in 'The Myth'?","Starred in 'Murder'?","Mallika Sherawat"},
            {"Granddaughter of Prime Minister$of Nepal?","First movie was Saudagar?","Starred in 1942 A Love Story?","Manisha Koirala"},
            {"Starred in Well 'Done Abba'?","Starred in 'Honey Travel$Pvt Ltd'?","Debuted in  film 'Yahaan'?","Minissha Lamba"},
            {"Debut in film Rockstar?","Czech father Pak mother?","Posed for 2009 Kingfisher$Swimsuit Calendar?","Nargis Fakhri"},
            {"Winner of Femina Miss India 2002?","Debut in film 'Qayamat:$City Under Threat'?","Special Appearance in De Taali?","Neha Dhupia"},
            {"Best known as Bani Walia in$Hindi TV drama?","Debut in film 'Rock On'?","Starred in 'Once Upon a$Time in Mumbaai'?","Prachi Desai"},
            {"Won Miss World title in 2000?","Won  National Award for 'Fashion'?","Won Best Villain Award for$'Aitraaz'?","Priyanka Chopra"},
            {"Daughter of Kamal and Sarika?","Starred in Dil Toh$Baccha Hai Ji?","Debut with Luck?","Shruti Haasan"},
            {"Daughter of Sharmila Tagore?","Daughter of Tiger Pataudi?","Acting debut with$'Dil Maange More'?","Soha Ali"},
            {"Rajjo in 'Dabangg'?","Daughter of Shatruji?","Debut with 'Dabangg'?","Sonakshi Sinha"},
            {"Daughter of Anil Kapoor?","Acting Debut with Saawariya?","Starred in Delhi 6?","Sonam Kapoor"},
            {"Debut with 'Parineeta'?","Radio-jockey in 'Munna bhai MBBS'?","Mother of Amitabh Bachchan$in 'Paa'?","Vidya Balan"},
            {"Debut with film 'Veer'?","Known for item number Character Dheela?","Looks very similar to Katrina Kaif?","Zarine Khan"},
            {"Debut with 'Boom'?","Starred in 'Zindagi Na Milegi Dobara'?","British Indian actress?","Katrina Kaif"},
            /*{R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.},
            {R.drawable.}*/
    };

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        int imageHeight = (int) (options.outHeight * GlobalVariables.yScale_factor);
        int imageWidth = (int) (options.outWidth * GlobalVariables.xScale_factor);
        options.inJustDecodeBounds = false;
        return Utility.getResizedBitmap(BitmapFactory.decodeResource(res, resId, options), imageWidth, imageHeight);
    }
}
