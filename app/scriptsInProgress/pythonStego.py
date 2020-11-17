#!/usr/bin/env python
# -*- coding: utf-8 -*-

import click
import qrcode
from PIL import Image


class Steganography(object):

    # Generate a qr code to embed
    @staticmethod
    def qrgen(input):
        img2 = qrcode.make(input)
        return img2

    @staticmethod
    def __int_to_bin(rgb):
        """Convert an integer tuple to a binary (string) tuple.

        :param rgb: An integer tuple (e.g. (220, 110, 96))
        :return: A string tuple (e.g. ("00101010", "11101011", "00010110"))
        """
        r, g, b = rgb
        return ('{0:08b}'.format(r),
                '{0:08b}'.format(g),
                '{0:08b}'.format(b))

    @staticmethod
    def __bin_to_int(rgb):
        """Convert a binary (string) tuple to an integer tuple.

        :param rgb: A string tuple (e.g. ("00101010", "11101011", "00010110"))
        :return: Return an int tuple (e.g. (220, 110, 96))
        """
        r, g, b = rgb
        return (int(r, 2),
                int(g, 2),
                int(b, 2))

    @staticmethod
    def __merge_rgb(rgb1, rgb2):
        """Merge two RGB tuples.

        :param rgb1: A string tuple (e.g. ("00101010", "11101011", "00010110"))
        :param rgb2: Another string tuple
        (e.g. ("00101010", "11101011", "00010110"))
        :return: An integer tuple with the two RGB values merged.
        """
        r1, g1, b1 = rgb1
        r2, g2, b2 = rgb2
        # This is excessive, we only need the least significant bit to embed black or white
        # rgb = (r1[:4] + r2[:4],
        #        g1[:4] + g2[:4],
        #        b1[:4] + b2[:4])
        if r2[1] == '0':
            rgb = (r1[:7] + '0',
                   g1[:7] + '0',
                   b1[:7] + '0')
        else:
            rgb = (r1[:7] + '1',
                   g1[:7] + '1',
                   b1[:7] + '1')
        return rgb

    @staticmethod
    def __black_or_white(pix):
        if (pix == 255):
            value = ("11111111","11111111","11111111")
        else:
            value = ("00000000","00000000","00000000")
        return value

    @staticmethod
    def __0_or_255(pix):
        if (pix == '0'):
            value = 0
        else:
            value = 255
        return value

    @staticmethod
    def merge(img1):
        """Merge two images. The second one will be merged into the first one.

        :param img1: First image
        :param img2: Second image
        :return: A new merged image.
        """
        img2 = Steganography.qrgen('12345')
        img2.save('qrcode.png')

        # Check the images dimensions
        if img2.size[0] > img1.size[0] or img2.size[1] > img1.size[1]:
            raise ValueError('Image 2 should not be larger than Image 1!')

        # Get the pixel map of the two images
        pixel_map1 = img1.load()
        pixel_map2 = img2.load()

        # Create a new image that will be outputted
        new_image = Image.new(img1.mode, img1.size)
        pixels_new = new_image.load()

        for i in range(img2.size[0]):
            for j in range(img2.size[1]):
                rgb1 = Steganography.__int_to_bin(pixel_map1[i, j])

                # Use a black pixel as default
                rgb2 = Steganography.__int_to_bin((0, 0, 0))

                # Check if the pixel map position is valid for the second image
                if i < img2.size[0] and j < img2.size[1]:
                    rgb2 = Steganography.__black_or_white(pixel_map2[i, j])

                # Merge the two pixels and convert it to a integer tuple
                rgb = Steganography.__merge_rgb(rgb1, rgb2)

                pixels_new[i, j] = Steganography.__bin_to_int(rgb)

        for i in range(img2.size[0],img1.size[0]):
            for j in range (img2.size[1],img1.size[1]):
                pixels_new[i,j]=pixel_map1[i,j]
        for i in range(0,img2.size[0]):
            for j in range(img2.size[1],img1.size[1]):
                pixels_new[i,j]=pixel_map1[i,j]
        for i in range(img2.size[0],img1.size[0]):
            for j in range(0,img2.size[1]):
                pixels_new[i,j]=pixel_map1[i,j]

        return new_image

    @staticmethod
    def unmerge(img):
        """Unmerge an image.

        :param img: The input image.
        :return: The unmerged/extracted image.
        """

        # Load the pixel map
        pixel_map = img.load()

        # Create the new image and load the pixel map
        new_image = Image.new(img.mode, (290,290))
        pixels_new = new_image.load()

        # Tuple used to store the image original size
        original_size = img.size

        for i in range(290):
            for j in range(290):
                # Get the RGB (as a string tuple) from the current pixel
                r, g, b = Steganography.__int_to_bin(pixel_map[i, j])

                # Irrelevant to our needs
                #
                # Extract the last 4 bits (corresponding to the hidden image)
                # Concatenate 4 zero bits because we are working with 8 bit
                # rgb = (r[4:] + '0000',
                #        g[4:] + '0000',
                #        b[4:] + '0000')

                # Convert it to an integer tuple
                pixels_new[i, j] = (Steganography.__0_or_255(r[-1]),Steganography.__0_or_255(g[-1]),Steganography.__0_or_255(b[-1]))

                # If this is a 'valid' position, store it
                # as the last valid position
                if pixels_new[i, j] != (0, 0, 0):
                    original_size = (i + 1, j + 1)

        # Crop the image based on the 'valid' pixels
        new_image = new_image.crop((0, 0, original_size[0], original_size[1]))

        return new_image


@click.group()
def cli():
    pass


@cli.command()
@click.option('--img1', required=True, type=str, help='Image that will hide another image')
@click.option('--output', required=True, type=str, help='Output image')
def merge(img1, output):
    merged_image = Steganography.merge(Image.open(img1))
    merged_image.save(output)


@cli.command()
@click.option('--img', required=True, type=str, help='Image that will be hidden')
@click.option('--output', required=True, type=str, help='Output image')
def unmerge(img, output):
    unmerged_image = Steganography.unmerge(Image.open(img))
    unmerged_image.save(output)


if __name__ == '__main__':
    cli()
