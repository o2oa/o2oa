package com.facepp.demo.util;

import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class PointsMatrix {
	private final String vertexShaderCode =
			// This matrix member variable provides a hook to manipulate
			// the coordinates of the objects that use this vertex shader
			"uniform mat4 uMVPMatrix;" +
					"attribute vec4 vPosition;" + "void main() {" +
					// the matrix must be included as a modifier of gl_Position
					"  gl_Position = vPosition * uMVPMatrix; gl_PointSize = 8.0;" + "}";

	private final String fragmentShaderCode = "precision mediump float;" + "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	// private final FloatBuffer vertexBuffer;
	private final ShortBuffer drawListBuffer, drawLineListBuffer, cubeListBuffer[] = new ShortBuffer[5];
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float squareCoords[] = { -0.2f, 0.2f, 0.0f, // top left
			-0.2f, -0.2f, 0.0f, // bottom left
			-0.4f, -0.2f, 0.0f, // bottom right
			-0.4f, 0.2f, 0.0f }; // top right
	static float squareCoords_1[] = { 0.2f, 0.2f, 0.0f, // top left
			0.2f, -0.2f, 0.0f, // bottom left
			0.4f, -0.2f, 0.0f, // bottom right
			0.4f, 0.2f, 0.0f }; // top right

	private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw
	// vertices

	private final short drawLineOrder[] = { 0, 1, 0, 2, 0, 3}; // order to draw
	// vertices


	private final short cubeOrders[][] = {{ 0, 1 }, { 0, 2 },
			{ 0, 3 } };

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
	// vertex

	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
	float color_rect[] = { 0X61 / 255.0f, 0XB3 / 255.0f, 0X4D / 255.0f, 1.0f };
	float color_megvii[][] = {
			{ 0f, 0f, 0f, 1.0f },
			{ 1.0f, 1.0f, 1.0f, 1.0f },
			{ 1f, 0f, 0.f, 1.0f }, //red
			{ 0.0f, 1f, 0.f, 1.0f }, //green
			{ 0.0f, 0.0f, 1f, 1.0f } }; // blue

	// 画点
	public ArrayList<ArrayList> points = new ArrayList<ArrayList>();
	// 画框
	public ArrayList<FloatBuffer> vertexBuffers = new ArrayList<FloatBuffer>();
	// 画底部矩形
	public FloatBuffer bottomVertexBuffer;

	// public ArrayList<FloatBuffer> vertexBuffers = new
	// ArrayList<FloatBuffer>();

	// public void setSquareMatrix(float[] squareCoords){
	// vertexBuffer.put(squareCoords);
	// vertexBuffer.position(0);
	// }
	private boolean isFaceCompare;

	public boolean isShowFaceRect;

	// 人脸矩形
	public Rect rect;

	public ArrayList<FloatBuffer> faceRects;
	private ShortBuffer faceRectListBuffer;

	private final short drawFaceRectOrder[] = {0, 1, 1, 2, 2, 3, 3, 0};


	public PointsMatrix(boolean isFaceCompare) {
		// FloatBuffer fb_0 = floatBufferUtil(squareCoords);
		// FloatBuffer fb_1 = floatBufferUtil(squareCoords_1);
		// vertexBuffers.add(fb_0);
		// vertexBuffers.add(fb_1);
		// initialize byte buffer for the draw list
		this.isFaceCompare = isFaceCompare;

		ByteBuffer dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);
		ByteBuffer line_dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				drawLineOrder.length * 2);
		line_dlb.order(ByteOrder.nativeOrder());
		drawLineListBuffer = line_dlb.asShortBuffer();
		drawLineListBuffer.put(drawLineOrder);
		drawLineListBuffer.position(0);
		for (int i = 0; i < cubeOrders.length; ++i) {
			final short cubeOrder[] = cubeOrders[i];
			ByteBuffer cubedlb = ByteBuffer.allocateDirect(
					// (# of coordinate values * 2 bytes per short)
					cubeOrder.length * 2);
			cubedlb.order(ByteOrder.nativeOrder());
			cubeListBuffer[i] = cubedlb.asShortBuffer();
			cubeListBuffer[i].put(cubeOrder);
			cubeListBuffer[i].position(0);
		}

		ByteBuffer faceRectLDB = ByteBuffer.allocateDirect(drawFaceRectOrder.length * 2);
		faceRectLDB.order(ByteOrder.nativeOrder());
		faceRectListBuffer = faceRectLDB.asShortBuffer();
		faceRectListBuffer.put(drawFaceRectOrder);
		faceRectListBuffer.position(0);

		// prepare shaders and OpenGL program
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
		// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
		// shader to program
		GLES20.glLinkProgram(mProgram); // create OpenGL program executables
	}

	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		checkGlError("glUniformMatrix4fv");
		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color_rect, 0);

		synchronized (this) {
			for (int i = 0; i < vertexBuffers.size(); i++) {
				FloatBuffer vertexBuffer = vertexBuffers.get(i);
				if (vertexBuffer != null) {
					// Prepare the triangle coordinate data
					GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
							vertexStride, vertexBuffer);
					// Draw the square

					GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT,
							drawListBuffer);
				}
			}
		}

		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		if (!isFaceCompare&&!isShowFaceRect){    //这里在绘制判断，需要调用api判断
			synchronized (this) {
				for (int i = 0; i < points.size(); i++) {
					ArrayList<FloatBuffer> triangleVBList = points.get(i);
					for (int j = 0; j < triangleVBList.size(); j++) {
						FloatBuffer fb = triangleVBList.get(j);
						if (fb != null) {
							GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, fb);
							// Draw the point
							GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
						}
					}
				}
			}
		}


		synchronized (this) {
			if (bottomVertexBuffer != null) {
				GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride,
						bottomVertexBuffer);
//				GLES20.glUniform4fv(mColorHandle, 1, color_megvii[1], 0);
//				GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT,
//						drawListBuffer);
//				GLES20.glUniform4fv(mColorHandle, 1, color_megvii[0], 0);
//				GLES20.glDrawElements(GLES20.GL_LINES, drawLineOrder.length, GLES20.GL_UNSIGNED_SHORT,
//						drawLineListBuffer);
				// Draw the square
				GLES20.glLineWidth(4.0f);
				for (int i = 0; i < cubeOrders.length; ++i) {
					GLES20.glUniform4fv(mColorHandle, 1, color_megvii[i + 2], 0);
					GLES20.glDrawElements(GLES20.GL_LINES, cubeOrders[i].length, GLES20.GL_UNSIGNED_SHORT,
							cubeListBuffer[i]);
				}
			}
		}


		synchronized (this){
			if (faceRects != null && faceRects.size()>0){
				GLES20.glLineWidth(4.0f);
				GLES20.glUniform4f(mColorHandle, 1.0f, 0.0f, 0.0f, 1.0f);

				for(int i = 0; i < faceRects.size(); i++){
					FloatBuffer buffer = faceRects.get(i);
					GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, buffer);
					GLES20.glDrawElements(GLES20.GL_LINES, drawFaceRectOrder.length, GLES20.GL_UNSIGNED_SHORT, faceRectListBuffer);

				}
			}
		}



			// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	public int loadShader(int type, String shaderCode) {
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("ceshi", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	// 定义一个工具方法，将float[]数组转换为OpenGL ES所需的FloatBuffer
	public FloatBuffer floatBufferUtil(float[] arr) {
		// 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
		ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
		// 数组排列用nativeOrder
		qbb.order(ByteOrder.nativeOrder());
		FloatBuffer mBuffer = qbb.asFloatBuffer();
		mBuffer.put(arr);
		mBuffer.position(0);
		return mBuffer;
	}

}
