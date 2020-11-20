package com.example.stegosaur;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

public class Steganography {

    /**
     * This method will encode the given qrCode image into the pixel data of the given ogImg image.
     *
     * @param ogImg     The original image to encode the data within
     * @param qrCode    The QR code image to embed
     * @return          The new image containing the embedded QR code
     */
    public static Bitmap encode(Bitmap ogImg, Bitmap qrCode) {
        // Initialize arrays to hold pixel data
        int[] ogPixels = new int[ogImg.getWidth() * ogImg.getHeight()];
        int[] qrPixels = new int[qrCode.getWidth() * qrCode.getHeight()];
        int[] stegoPixels = new int[ogImg.getWidth() * ogImg.getHeight()];
        // Initialize new image to contain result of encoding
        Bitmap stegoImg = null;
        // Grab the pixels of the original image and qr code
        try {
            ogImg.getPixels(ogPixels, 0, ogImg.getWidth(), 0, 0, ogImg.getWidth(),
                    ogImg.getHeight());
            qrCode.getPixels(qrPixels, 0, qrCode.getWidth(), 0, 0, qrCode.getWidth(),
                    qrCode.getHeight());
        } catch (Exception e) {
            System.err.println("Error grabbing pixels during encoding.");
        }
        // Using pixel data and image dimensions, write the qr code information into the least
        // significant bit of the pixel data
        for (int j = 0; j < ogImg.getHeight(); j++) {
            for (int i = 0; i < ogImg.getWidth(); i++) {
                if ((j * ogImg.getWidth() + i) < qrPixels.length) {
                    stegoPixels[j * ogImg.getWidth() + i] = encodeSinglePixel(ogPixels[j
                            * ogImg.getWidth() + i], qrPixels[j * ogImg.getWidth() + i]);
                } else {
                    stegoPixels[j * ogImg.getWidth() + i] = ogPixels[j * ogImg.getWidth() + i];
                }
            }
        }
        // Turn the array of pixels back into an image
        stegoImg = arrayToImage(stegoPixels, ogImg.getWidth(), ogImg.getHeight());
        return stegoImg;
    }

    /**
     * This method will split the given ogPixel pixel into separate channels: alpha, red, green and
     * blue. The qrPixel pixel color will be stored in the least significant bit of the ogPixel
     * pixel data. The new pixel with the embedded information will be returned.
     *
     * @param ogPixel   The pixel from the original image
     * @param qrPixel   The pixel from the QR code image
     * @return          The new pixel with the QR code pixel data embedded
     */
    public static int encodeSinglePixel(int ogPixel, int qrPixel) {
        // Split pixel into separate channels
        int ogAlpha = (ogPixel >> 24) & 0xff;
        int ogRed   = (ogPixel >> 16) & 0xff;
        int ogGreen = (ogPixel >> 8) & 0xff;
        int ogBlue = (ogPixel) & 0xff;
        int qrRed = (qrPixel >> 16) & 0xff;
        // Convert pixel int values to binary
        String ogRedBits = Integer.toBinaryString(ogRed);
        String ogBlueBits = Integer.toBinaryString(ogBlue);
        String ogGreenBits = Integer.toBinaryString(ogGreen);
        String qrRedBits = Integer.toBinaryString(qrRed);
        // Check if the qr pixel is black or white, otherwise throw error
        // Append a 1 or 0 to the new pixel, depending on the qr code pixel value
        String newPixelRedBits = blackOrWhiteMerge(ogRedBits, qrRedBits);
        String newPixelBlueBits = blackOrWhiteMerge(ogBlueBits, qrRedBits);
        String newPixelGreenBits = blackOrWhiteMerge(ogGreenBits, qrRedBits);
        // Convert pixel binary values to int
        int newPixelRed = Integer.parseInt(newPixelRedBits,2);
        int newPixelBlue = Integer.parseInt(newPixelBlueBits,2);
        int newPixelGreen = Integer.parseInt(newPixelGreenBits,2);
        // Combine separate channels into a pixel
        return ((ogAlpha << 24) | (newPixelRed << 16) | (newPixelGreen << 8) | (newPixelBlue));
    }

    /**
     * This method will decode the qrCode image hidden in the pixel data of the given ogImg image.
     *
     * @param stegoImg  The image containing the embedded QR code
     * @param dummy     TODO: Dummy QR Code we should remove
     * @return          The QR code image
     */
    public static Bitmap decode(Bitmap stegoImg, Bitmap dummy) {
        // TODO: We should generate QR codes to a fixed size, then hardcode those dimensions here
        // Initialize arrays to hold pixel data
        int[] stegoPixels = new int[stegoImg.getWidth() * stegoImg.getHeight()];
        int[] qrPixels = new int[dummy.getWidth() * dummy.getHeight()];
        // Initialize new image to contain result of decoding
        Bitmap qrCode = null;
        // Grab the pixels of the image to decode
        try {
            stegoImg.getPixels(stegoPixels, 0, stegoImg.getWidth(), 0, 0,
                    stegoImg.getWidth(), stegoImg.getHeight());
        } catch (Exception e) {
            System.err.println("Error grabbing pixels during decoding.");
        }
        // Pull the qr code pixel data from the least significant bit of the pixels in the area
        for (int j = 0; j < stegoImg.getHeight(); j++) {
            for (int i = 0; i < stegoImg.getWidth() && ((j * stegoImg.getWidth() + i)
                    < qrPixels.length); i++) {
                qrPixels[j * stegoImg.getWidth() + i] = decodeSinglePixel(stegoPixels[j
                        * stegoImg.getWidth() + i]);
            }
        }
        // Turn the array of pixels back into an image
        qrCode = arrayToImage(qrPixels, dummy.getWidth(), dummy.getHeight());
        return qrCode;
    }

    /**
     * This method will split the given stegoPixel pixel into separate channels but only handle
     * the red channel. Each channel contains the QR code pixel information for redundancy.
     *
     * @param stegoPixel    The pixel containing the embedded QR code pixel
     * @return              The decoded QR code pixel
     */
    public static int decodeSinglePixel(int stegoPixel) {
        // Will return white pixel by default
        int qrPixel = 0;
        // Each pixel contains the information, so we only need to check one
        int stegoRed   = (stegoPixel >> 16) & 0xff;
        // Convert pixel int value to binary
        String stegoRedBits = Integer.toBinaryString(stegoRed);
        // Check for black or white
        if (stegoRedBits.charAt(stegoRedBits.length()-1) == '1') {
            qrPixel = 255;
        }
        // Return the decoded pixel
        return ((255 << 24) | (qrPixel << 16) | (qrPixel << 8) | (qrPixel));
    }

    /**
     * This method will check the value of the qrPixelBits binary pixel data and hide it in the
     * ogPixelBits binary pixel data by replacing the least significant bit with a 1, for black, or
     * a 0, for white. In the case of missing leading zeroes, the method will prepend them.
     *
     * @param ogPixelBits   The pixel from the original image in binary
     * @param qrPixelBits   The pixel from the QR code image in binary
     * @return              The pixel binary data with the QR code pixel data hidden within
     */
    public static String blackOrWhiteMerge(String ogPixelBits, String qrPixelBits) {
        // Initialize string builder and append the image pixel bit to it
        StringBuilder sb = new StringBuilder();
        sb.append(ogPixelBits);
        // Leading zeros aren't always included, this loop will correct that
        while (sb.length() <= 8) {
            sb.insert(0, '0');
        }
        ogPixelBits = sb.toString();
        sb.setLength(0);
        // Append a one or zero to the pixel information based on the value of the qr code pixel
        if (qrPixelBits.equals("11111111")) {
            sb.append(ogPixelBits.substring(0,8));
            sb.append(1);
        } else if (qrPixelBits.equals("0")) {
            sb.append(ogPixelBits.substring(0,8));
            sb.append(0);
        } else {
            System.err.println("Corrupted QR data detected.");
        }
        return sb.toString();
    }

    /**
     * This method will create a Bitmap image from an array of pixel information. API version 26 is
     * targeted so the compiler won't complain about the usage of RGBA_F16, an essential component
     * to read and process the pixel array.
     *
     * @param stegoPixels   The pixel array to be turned into an image
     * @param width         The desired width of the resulting image
     * @param height        The desired height of the resulting image
     * @return              The Bitmap image result
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static Bitmap arrayToImage(int[] stegoPixels, int width, int height) {
        return Bitmap.createBitmap(stegoPixels, width, height, Bitmap.Config.RGBA_F16);
    }
}