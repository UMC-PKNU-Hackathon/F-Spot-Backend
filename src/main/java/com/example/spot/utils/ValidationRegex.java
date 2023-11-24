package doubleni.mealrecipe.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    public static boolean isRegexPhone(String target){
        String regex = "^[0-9]{9,11}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    public static boolean isRegexPassword(String target){
        String lenReg = "[a-zA-Z0-9!@#$%^&*]{8,20}";
        String engReg = "[a-zA-Z]";
        String numReg = "[0-9]";
        String specReg = "[!@#$%^&*]";

        int has =0;
        if(Pattern.matches(lenReg, target)){
            has+=Pattern.compile(engReg).matcher(target).find() ? 1:0;
            has+=Pattern.compile(numReg).matcher(target).find() ? 1:0;
            has+=Pattern.compile(specReg).matcher(target).find() ? 1:0;

            if(has < 2){
                //System.out.println("영문,숫자,특수문자(!@#$%^&*) 중 두 가지 이상이 조합하여 입력하세요");
                return false;
            }else{
                //  System.out.println("적합한 비번입니다");
                return true;
            }
        }else{
            //System.out.println("영문,숫자,특수문자(!@#$%^&*)로 8자에서 20자 입력하세요");
            return false;
        }

    }
}
