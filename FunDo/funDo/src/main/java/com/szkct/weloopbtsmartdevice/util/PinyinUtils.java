package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;

import com.kct.fundo.btnotification.R;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/12/12
 * 描述: ${VERSION}
 * 修订历史：
 */

public class PinyinUtils {

    public static Map<String,String> dictionary = new HashMap<String,String>();

    //加载多音字词典
    public static void init(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.duoyinzi_pinyin),"UTF-8"));
                    String line = null;
                    while((line=br.readLine())!=null){

                        String[] arr = line.split("#");

                        if(!StringUtils.isEmpty(arr[1])){
                            String[] sems = arr[1].split(" ");
                            for (String sem : sems) {

                                if(!StringUtils.isEmpty(sem)){
                                    dictionary.put(sem , arr[0]);
                                }
                            }
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(br!=null){
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }/////
        }).start();
    }

    public static String[] chineseToPinYin(char chineseCharacter) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        if(chineseCharacter<=128){    //ASCII >=33 ASCII<=125的直接返回 ,ASCII码表：http://www.asciitable.com/
            return new String[]{String.valueOf(chineseCharacter)};
        }


        return PinyinHelper.toHanyuPinyinStringArray(chineseCharacter, outputFormat);
    }


    /**
     * 获取汉字拼音的全拼
     * @param chineseCharacter
     * @return
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String chineseToPinYinF(Context context,String chineseCharacter) throws BadHanyuPinyinOutputFormatCombination{
        if(StringUtils.isEmpty(chineseCharacter)){
            return null;
        }

        char[] chs = chineseCharacter.toCharArray();

        StringBuilder result = new StringBuilder();

        init(context);

        for(int i=0;i<chs.length;i++){
            String[] arr = chineseToPinYin(chs[i]);
            if(arr==null){
                result.append("");
            }else if(arr.length==1){
                result.append(arr[0]);
            }else if(arr[0].equals(arr[1])){
                result.append(arr[0]);
            }else{

                String prim = chineseCharacter.substring(i, i+1);
//              System.out.println("prim="+prim+"**i="+i);

                String lst = null,rst = null;

                if(i<=chineseCharacter.length()-2){
                    rst = chineseCharacter.substring(i,i+2);
                }
                if(i>=1 && i+1<=chineseCharacter.length()){
                    lst = chineseCharacter.substring(i-1,i+1);
                }

//              System.out.println("lst="+lst+"**rst="+rst);

                String answer = null;
                for (String py : arr) {

                    if(StringUtils.isEmpty(py)){
                        continue;
                    }

                    if((lst!=null && py.equals(dictionary.get(lst))) ||
                            (rst!=null && py.equals(dictionary.get(rst)))){
                        answer = py;
//                      System.out.println("get it,answer="+answer+",i="+i+"**break");
                        break;
                    }

                    if(py.equals(dictionary.get(prim))){
                        answer = py;
//                      System.out.println("get it,answer="+answer+",i="+i+"**prim="+prim);
                    }
                }
                if(answer!=null){
                    answer = answer.toUpperCase().charAt(0)+answer.substring(1);
                    result.append(answer);
                }
            }
        }

        return result.toString().toLowerCase();
    }

}
