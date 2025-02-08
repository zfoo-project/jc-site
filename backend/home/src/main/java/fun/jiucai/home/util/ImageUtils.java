/*
 * Copyright (C) 2020 The zfoo Authors
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package fun.jiucai.home.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * @author godotg
 */
@Slf4j
public abstract class ImageUtils {


    @SneakyThrows
    public static byte[] resize(byte[] source, float ratio) {
        // 读取源图片
        var sourceImage = ImageIO.read(new ByteArrayInputStream(source));

        // 获取图片的宽和高
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        int newWidth = (int) (width * ratio); // 新宽度
        int newHeight = (int) (height * ratio); // 新高度

        // 创建新图片对象
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, sourceImage.getType());

        // 获取图形上下文对象
        Graphics2D g2d = resizedImage.createGraphics();
        try {
            // 绘制新图片
            g2d.drawImage(sourceImage, 0, 0, newWidth, newHeight, null);
        } finally {
            g2d.dispose();
        }

        // 保存新图片
        var byteArrayOutputStream = new ByteArrayOutputStream((int) (source.length * ratio));
        ImageIO.write(resizedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * 将图片转为jpg，并且压缩
     *
     * @param source             原图片
     * @param compressionQuality 压缩质量，0.0f到1.0f之间
     * @return 压缩后的字节数组
     */
    public static byte[] compress(byte[] source, float compressionQuality) {
        var bytes = compress(source, compressionQuality, "jpg");
        if (bytes == null) {
            bytes = compress(source, compressionQuality, "png");
        }
        if (bytes == null) {
            bytes = compress(source, compressionQuality, "jpeg");
        }
        if (bytes == null) {
            bytes = compress(source, compressionQuality, "tiff");
        }
        if (bytes == null) {
            bytes = source;
        }
        return bytes;
    }

    private static byte[] compress(byte[] source, float compressionQuality, String imageType) {
        try {
            // 创建压缩参数对象，并设置压缩质量
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(imageType);
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(compressionQuality);

            // 创建压缩输出流
            ByteArrayOutputStream byteArrayOutputStream = null;
            ImageOutputStream outputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayOutputStream((int) (source.length * compressionQuality));
                outputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
                writer.setOutput(outputStream);
                // 将源图片写入压缩输出流
                var sourceImage = ImageIO.read(new ByteArrayInputStream(source));
                writer.write(null, new IIOImage(sourceImage, null, null), param);
            } finally {
                // 关闭流
                writer.dispose();
                byteArrayOutputStream.close();
                outputStream.close();
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable t) {
            log.error("压缩[{}]图片未知错误，返回原始图片", imageType, t);
            return null;
        }
    }

}
