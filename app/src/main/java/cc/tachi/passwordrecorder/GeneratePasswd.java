package cc.tachi.passwordrecorder;

import java.util.Random;

/**
 * Created by m on 2016/11/7.
 */
public class GeneratePasswd {
    public String generate(int length,int method){
        Random r = new Random();
        String symbol = "!@#$%^&*()_+-={}[]:\"\\;'<>?,./~";
        String result="";
        switch (method){
            //1,2,4,8数字小写大写符号
            case 1:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextInt(10)+48);
                }
                break;
            case 2:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextInt(25)+65+32);
                }
                break;
            case 3:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65+32));
                }
                break;
            case 4:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextInt(25)+65);
                }
                break;
            case 5:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65));
                }
                break;
            case 6:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextBoolean()?(r.nextInt(25)+65+32):(r.nextInt(25)+65));
                }
                break;
            case 7:
                for(int i = 0;i<length;i++){
                    result+=(char)(r.nextBoolean()?(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65+32)):r.nextInt(25)+65);
                }
                break;
            case 8:
                for(int i = 0;i<length;i++){
                    result+=symbol.charAt(r.nextInt(30));
                }
                break;
            case 9:
                for(int i = 0;i<length;i++){
                    result+=r.nextBoolean()?(char)(r.nextInt(10)+48):symbol.charAt(r.nextInt(30));
                }
                break;
            case 10:
                for(int i = 0;i<length;i++){
                    result+=r.nextBoolean()?(char)(r.nextInt(10)+48):(char)(r.nextInt(25)+65+32);
                }
                break;
            case 11:
                for(int i = 0;i<length;i++){
                    result+=r.nextBoolean()?(char)(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65+32)):symbol.charAt(r.nextInt(30));
                }
                break;
            case 13:
                for(int i = 0;i<length;i++){
                    result+=r.nextBoolean()?(char)(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65)):symbol.charAt(r.nextInt(30));
                }
                break;
            case 14:
                for(int i = 0;i<length;i++){
                    result+=r.nextBoolean()?(char)(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65)):(char)(r.nextInt(25)+65+32);
                }
                break;
            case 15:
                for(int i = 0;i<length;i++){
                    result+=r.nextBoolean()?(char)(r.nextBoolean()?(r.nextInt(10)+48):(r.nextInt(25)+65)):r.nextBoolean()?(char)(r.nextInt(25)+65+32):symbol.charAt(r.nextInt(30));
                }
                break;
            default:
                break;
        }
        return result;
    }
}
