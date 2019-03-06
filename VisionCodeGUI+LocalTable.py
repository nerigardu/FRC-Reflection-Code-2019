#!/usr/bin/python3

# Note: The contour filter is based off of a 30x30 square
from grip import GripPipeline
from networktables import NetworkTables
import cv2
import numpy
import os

# Note:
# Coordinates of BoundingRect are based off of Quadrant IV (+ Bottom right)

# Change booleans to enable or disable cetain functions
# Note that corners will not be published if corresponding processing is False
printValues = True
usingBoundingRect = True
usingMinAreaRect = False
publishingCornersMin = False
enableGUI = True

# Video device assignment (Change accordingly)
# (NEED TO CHANGE OS COMMAND BELOW AS WELL):
videoDevice = 1

# NetworkTables server assignment (Change accordingly):
server = '127.0.0.1'


def publishData(pipeline):

    if usingBoundingRect:
        boundingRect_FULL = []
        center_x_positions = []
        center_y_positions = []
        widths = []
        heights = []

    if usingMinAreaRect:
        minAreaRect_FULL = []
        rect_center = []
        width_height = []
        angle_of_rotation = 0

    if publishingCornersMin:
        MIN_corner_a = []
        MIN_corner_b = []
        MIN_corner_c = []
        MIN_corner_d = []

    # Find bounding rectangles of the contours to get variables
    for contour in pipeline.filter_contours_output:

        if usingBoundingRect:
            # Data mashed into one variable
            boundingRect_FULL = cv2.boundingRect(contour)
            # Data spread out over 4 variables
            x, y, w, h = cv2.boundingRect(contour)

            # (Technically) Extra processing for boundingRect variables
            # X and Y are coordinates of top-left corner of bounding box
            center_x_positions.append(x + w / 2)
            center_y_positions.append(y + h / 2)
            widths.append(w)
            heights.append(h)

        if usingMinAreaRect:
            # Data mashed into one variable
            minAreaRect_FULL = cv2.minAreaRect(contour)
            # Data spread out over 3 variables
            (rect_center,
             width_height,
             angle_of_rotation) = cv2.minAreaRect(contour)

            if publishingCornersMin:
                (MIN_corner_a,
                 MIN_corner_b,
                 MIN_corner_c,
                 MIN_corner_d) = cv2.boxPoints(minAreaRect_FULL)

    # Publish to NetworkTables and print out data (if enabled):
    if usingBoundingRect:
        tableBound = NetworkTables.getTable('/vision/boundingRect')
        tableBound.putNumberArray('center_x_positions', center_x_positions)
        tableBound.putNumberArray('center_y_positions', center_y_positions)
        tableBound.putNumberArray('width', widths)
        tableBound.putNumberArray('height', heights)

        if printValues:
            print('---boundingRect Data---')
            print('boundingRect_FULL:')
            print(boundingRect_FULL)
            print('center_x_positions:')
            print(center_x_positions)
            print('center_y_positions:')
            print(center_y_positions)
            print('widths:')
            print(widths)
            print('heights:')
            print(heights)

            print('\n\n')

    if usingMinAreaRect:
        tableMin = NetworkTables.getTable('/vision/minAreaRect')
        tableMin.putNumberArray('rect_center', rect_center)
        tableMin.putNumberArray('width_height', width_height)
        tableMin.putNumber('angle_of_rotation', angle_of_rotation)

        if printValues:
            print('---minAreaRect Data---')
            print('minAreaRect_FULL:')
            print(minAreaRect_FULL)
            print('rect_center:')
            print(rect_center)
            print('width_height:')
            print(width_height)
            print('angle_of_rotation:')
            print(angle_of_rotation)

        if publishingCornersMin:
            tableMinCorners = (NetworkTables
                               .getTable('/vision/minAreaRect/corners'))
            tableMinCorners.putNumberArray('MIN_corner_a', MIN_corner_a)
            tableMinCorners.putNumberArray('MIN_corner_b', MIN_corner_b)
            tableMinCorners.putNumberArray('MIN_corner_c', MIN_corner_c)
            tableMinCorners.putNumberArray('MIN_corner_d', MIN_corner_d)

            if printValues:
                print('MIN_corner_a:')
                print(MIN_corner_a)
                print('MIN_corner_b:')
                print(MIN_corner_b)
                print('MIN_corner_c:')
                print(MIN_corner_c)
                print('MIN_corner_d:')
                print(MIN_corner_d)

        if printValues:
            print('\n\n')


def main():
    print('Initializing NetworkTables...')
    NetworkTables.initialize(server=server)

    print('Creating Video Capture...')
    cap = cv2.VideoCapture(videoDevice)

    # Change Exposure (***change video device as needed***):
    os.system(
            'v4l2-ctl -d /dev/video1 -c exposure_auto=1 -c exposure_absolute=0'
            )

    print('Creating Pipeline...')
    pipeline = GripPipeline()

    print('Running Pipeline.')
    while cap.isOpened():
        have_frame, frame = cap.read()

        if have_frame:
            pipeline.process(frame)
            publishData(pipeline)

        if enableGUI:
            # Set thickness of lines
            thickness = 2

            # Draw the normal contours (Green):
            cv2.drawContours(
                    frame, pipeline.filter_contours_output,
                    -1, (0, 255, 0), thickness
                    )

            # Draw the proper rectangles:
            for contour in pipeline.filter_contours_output:

                # Minimum Bounding Rectangle:
                minAreaRect_FULL = cv2.minAreaRect(contour)
                boxCorners = cv2.boxPoints(minAreaRect_FULL)

                # Numpy Conversion:
                box = (numpy
                       .array(boxCorners)
                       .reshape((-1, 1, 2))
                       .astype(numpy.int32))

                # Draw rotated rectangle (Red):
                cv2.drawContours(frame, [box], -1, (0, 0, 255), thickness)

                # Normal Bounding Rectangle:
                x, y, w, h = cv2.boundingRect(contour)
                cv2.rectangle(frame, (x, y), (x+w, y+h),
                              (255, 0, 0),  thickness)

            # Show all data in a window:
            cv2.imshow('FRC Vision', frame)

            # Kill if 'q' is pressed:
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

    print('Capture closed.')


if __name__ == '__main__':
    main()
