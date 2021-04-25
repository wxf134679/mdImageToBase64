package core;

import sun.misc.BASE64Encoder;

import javax.imageio.stream.FileImageInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类<code>Doc</code>用于：TODO
 *
 * @author wangxiaofei
 * @version 8.6
 * @date 2021-04-25
 */
public class Convert {
    public static void main(String[] args) throws IOException {
        String dmPath = args[0];
        File dmFile = new File(dmPath);
        String path = dmFile.getParentFile().getAbsolutePath() + File.separator;
        String fileName = dmFile.getName();
        String newFilePath = path + "NEW_" + fileName;

        String base64Prefix = "data:image/png;base64,";

        BufferedReader in = new BufferedReader(new FileReader(dmPath));

        CharArrayWriter tempStream = new CharArrayWriter();
        String regex = "\\!\\[(.*?)\\]\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);

        String content;
        while ((content = in.readLine()) != null) {

            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                //读取图片路径
                String imageUrl = matcher.group(1);
                //解码
                String decodeImageUrl = URLDecoder.decode(imageUrl);
                String base64Image = convertImageToBase64(path + decodeImageUrl);
                base64Image = base64Prefix + base64Image;
                content = content.replace("(" + imageUrl + ")", "(" + base64Image + ")");
            }

            // 将该行写入内存
            tempStream.write(content);
            // 添加换行符
            tempStream.append(System.getProperty("line.separator"));
        }
        in.close();
        // 将内存中的流 写入 文件
        FileWriter out = new FileWriter(newFilePath);
        tempStream.writeTo(out);
        out.close();


    }

    /**
     * 转换图片到base64
     *
     * @param imagePath
     * @return
     * @throws IOException
     */
    private static String convertImageToBase64(String imagePath) throws IOException {
        File image = new File(imagePath);
        long size = image.length();
        byte[] by = new byte[(int) size];
        InputStream is = new FileInputStream(imagePath);
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.read(by);
        // 关闭流
        is.close();
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64String = encoder.encode(by);
        return base64String.replace("\n", "");
    }
}
