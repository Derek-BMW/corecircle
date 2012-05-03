package me.corecircle;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MovementView extends SurfaceView implements SurfaceHolder.Callback {

	private int xPos[] = new int[8];// ����xλ��
	private int yPos[] = new int[8];// ����yλ��
	private static int XPos[] = new int[8];// ����xλ��
	private static int YPos[] = new int[8];// ����yλ��
	// public static int x1,y1;
	private double xVel[] = new double[8];// ���⶯ʱ��x����
	private double yVel[] = new double[8];// ���⶯ʱ��y����

	private static int circleRadius;// Բ��İ뾶
	private static int width;// �������
	private static int height;// �����߶�
	private static int Height;// view�߶�
	private static int Width;// view���
	private int d;
	private int me = width / 2;// ��������λ��
	private int x0;
	private int y0;

	private static int[] score = { 0, 1, 2, 3, 4, 5, 6, 4, 8 };// ��ֵ
	private static int[] point = { 1, 4, 6, 2, 5, 7, 3, 1 };// point[7]Ϊ���յ�����
	private static String[] CoreMember = { "ME", "A", "B", "C", "D", "E", "F",
			"G" };
	private static int shan = 0;// ���ƿ�ʼ��˸
	private static int n = 7;// ���Ѹ���
	private static int h = 0;// ģ�ⴥ�����ź�
	private static int second = 1;// �ڶ��δ���
	private static int push = 0;// �������ѡ��
	private static int num1 = 0;// ����

	private Paint circlePaint;
	UpdateThread updateThread;

	public MovementView(Context context) {

		super(context);
		getHolder().addCallback(this);

		// circleRadius = 10;//����Բ��뾶Ϊ15

		circlePaint = new Paint();

	}

	public MovementView(Context context, AttributeSet attrs) {

		super(context);
		getHolder().addCallback(this);

		// circleRadius = 10;//����Բ��뾶Ϊ15

		circlePaint = new Paint();

	}

	@Override
	protected void onDraw(Canvas canvas) {

		int[] color = { Color.RED, Color.argb(255, 26, 251, 0),
				Color.argb(255, 255, 255, 0), Color.BLUE, Color.WHITE,
				Color.argb(255, 128, 0, 255), Color.argb(255, 255, 148, 40),
		        Color.argb(255, 255, 128, 192) };// 8����ɫ
		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	canvas.drawColor(Color.BLACK);//����ɫ
//    	canvas.drawColor(Color.argb(150,128,176,230));//����ɫ
//		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		//canvas.drawColor(Color.BLACK);// ����ɫ
//		Paint paint = new Paint();
//		// ����Դ�ļ�������λͼ
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.main1);
//		
//		if(bitmap!=null)
//		// ��ͼ
//		{canvas.drawBitmap(bitmap, 0, 0, paint);}

		// ///////////////////////////////////////////////h��������˶�/ֹͣ//////////////////////////////////////////////////////////////////
		if (h == 1)// С��ʼ����˶�
		{
			push = 0;
			for (int i = 0; i < n + 1; i++)// ����8��Բ��
			{
				RadialGradient radGrad = new RadialGradient(xPos[i], yPos[i],
						circleRadius, color[i], color[i],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[i], yPos[i], circleRadius, circlePaint);
			}
		} else if (h == 0)// С��ֹͣ����˶�
		{

			super.onDraw(canvas);// ����ԲȦ
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.GRAY);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);

			for (int j = 1; j < 9; j++) {
				canvas.drawCircle(width / 2, height / 2, (circleRadius + j
						* ((width / 2 - circleRadius) / 9)), paint);
			}

			for (int j = 1; j < n + 1; j++) {
				// ����
				paint.setColor(color[j]);
				paint.setStrokeWidth(3);
				canvas.drawLine(width / 2, height / 2, XPos[j], YPos[j], paint);
			}

			for (int j = 1; j < n + 1; j++) {

				// ���ֵĻ���
				Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				Typeface mType = Typeface.create(Typeface.MONOSPACE,
						Typeface.NORMAL);
				mPaint.setColor(Color.YELLOW);
				mPaint.setTextSize(10);
				canvas.drawText(CoreMember[j - 1], xPos[j], yPos[j] + 2
						* circleRadius, mPaint);
			}

			for (int i = 0; i < n + 1; i++)// ����8����ɫԲ��
			{
				RadialGradient radGrad = new RadialGradient(xPos[i], yPos[i],
						circleRadius, color[i], color[i],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[i], yPos[i], circleRadius, circlePaint);

			}

		}
		// ///////////////////////////////////////////push�����������/ֹͣ////////////////////////////////////////////////////////////

		if (shan == 1) {
			int nz;
			nz = num1 / 5;
			if (push < 5 * n + 5) {// ������ͼ
				Paint paint = new Paint();
				for (int i = 0; i < n + 1; i++)// ����7����ɫԲ��,7����ɫ��
				{
					RadialGradient radGrad = new RadialGradient(xPos[i],
							yPos[i], circleRadius, Color.GRAY, Color.GRAY,
							Shader.TileMode.MIRROR);// ��ɫ���ĵ�Ļ���
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[i], yPos[i], circleRadius,
							circlePaint);

					if (i < n)// ���ߵĻ���
					{
						paint.setColor(Color.GRAY);
						paint.setStrokeWidth(3);
						canvas.drawLine(width / 2, height / 2, XPos[i + 1],
								YPos[i + 1], paint);
					}

					for (int j = 1; j < n + 1; j++) {
						// ���ֵĻ���
						Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
						Typeface mType = Typeface.create(Typeface.MONOSPACE,
								Typeface.NORMAL);
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextSize(10);
						canvas.drawText(CoreMember[j - 1], xPos[j], yPos[j] + 2
								* circleRadius, mPaint);
					}

				}
				RadialGradient radGrad = new RadialGradient(xPos[0], yPos[0],
						circleRadius, color[0], color[0],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[0], yPos[0], circleRadius, circlePaint);

				if (push < 5)// ���ĺ�㣬����ȫ��
				{
					push++;
				} else if (push < 5 * (nz + 2) && push >= 5 * (nz + 1))// �������
				{

					if (nz != 0) // ��ԭ��һ����ɫ�㡢��Ϊ��ɫ
					{
						radGrad = new RadialGradient(xPos[point[nz]],
								yPos[point[nz]], circleRadius, Color.GRAY,
								Color.GRAY, Shader.TileMode.MIRROR);
						circlePaint.setShader(radGrad);
						canvas.drawCircle(xPos[point[nz]], yPos[point[nz]],
								circleRadius, circlePaint);

						paint.setColor(Color.GRAY);
						paint.setStrokeWidth(3);
						canvas.drawLine(width / 2, height / 2, XPos[point[nz]],
								YPos[point[nz]], paint);

					}

					// ��ǰ�㡢��Ϊ��ɫ
					paint.setColor(color[point[nz]]);
					paint.setStrokeWidth(3);
					canvas.drawLine(width / 2, height / 2, XPos[point[nz]],
							YPos[point[nz]], paint);

					radGrad = new RadialGradient(xPos[point[nz]],
							yPos[point[nz]], circleRadius + 5,
							color[point[nz]], color[point[nz]],
							Shader.TileMode.MIRROR);
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[point[nz]], yPos[point[nz]],
							circleRadius + 5, circlePaint);

					radGrad = new RadialGradient(xPos[0], yPos[0],
							circleRadius, color[0], color[0],
							Shader.TileMode.MIRROR);// ����˴����ػ���ɫ�㣬�ͻῴ����ԭ�Ͳ�ɫ�����ں����
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[0], yPos[0], circleRadius,
							circlePaint);
				}
				num1++;
				push++;
			} else// ��������ս��
			{
				for (int i = 0; i < n + 1; i++)// ����8����ɫԲ��
				{
					RadialGradient radGrad = new RadialGradient(xPos[i],
							yPos[i], circleRadius, color[i], color[i],
							Shader.TileMode.MIRROR);
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[i], yPos[i], circleRadius,
							circlePaint);

					Paint paint = new Paint();
					if (i > 0)// ���ߵĻ���
					{
						paint.setColor(color[i]);
						paint.setStrokeWidth(3);
						canvas.drawLine(width / 2, height / 2, XPos[i],
								YPos[i], paint);
					}

				}

				for (int i = 1; i < n + 1; i++) {
					// ���ֵĻ���
					Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
					Typeface mType = Typeface.create(Typeface.MONOSPACE,
							Typeface.NORMAL);
					mPaint.setColor(Color.YELLOW);
					mPaint.setTextSize(10);
					canvas.drawText(CoreMember[i - 1], XPos[i], YPos[i] + 2
							* circleRadius, mPaint);
				}

				RadialGradient radGrad = new RadialGradient(xPos[0], yPos[0],
						circleRadius, color[0], color[0],
						Shader.TileMode.MIRROR);// ����˴����ػ���ɫ�㣬�ͻῴ����ԭ�Ͳ�ɫ�����ں����
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[0], yPos[0], circleRadius, circlePaint);

				// ���ս��Բ����
				radGrad = new RadialGradient(xPos[point[nz]], yPos[point[nz]],
						circleRadius, color[point[nz]], color[point[nz]],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[point[nz]], yPos[point[nz]],
						circleRadius + 5, circlePaint);
			}
			// shan=0;

		}
	}

	public void updatePhysics() // ��������λ��
	{
		int x;
		int y;
		int D;
		for (int i = 0; i < n + 1; i++) {
			if (h == 1) {
				if (second == 1)// ���·�������ֵ
				{
					for (int j = 0; j < n + 1; j++) {
						double w = (Math.random() * 7);
						double angle = w * 2 * Math.PI / n;
						xVel[j] = ((Math.cos(angle)) * (circleRadius / 2));
						yVel[j] = ((Math.sin(angle)) * (circleRadius / 2));
					}

					// ���²������˳�򣬸�ֵ��point����
					for (int m = 0; m < n; m++) {
						point[m] = m + 1;
					}
					Random random = new Random();
					for (int m = 0; i < n; i++) {
						int p = random.nextInt(n);
						int tmp = point[m];
						point[m] = point[p];
						point[p] = tmp;
					}
					random = null;

					second = 0;

				} else {
					xPos[i] += xVel[i];
					yPos[i] += yVel[i];
				}
			} else if (h == 0) {
				for (int k = 1; k < n + 1; k++) {
					D = circleRadius + d * (9 - score[k - 1]); // y=(int)
																// ((Math.sin(Math.PI*s/i))*D);
					double angle = k * 2 * Math.PI / n;
					x = (int) ((Math.cos(angle)) * D);
					y = (int) ((Math.sin(angle)) * D);

					XPos[k] = x + width / 2;
					YPos[k] = y + height / 2;
				}

				xPos[i] = XPos[i];
				yPos[i] = YPos[i];
				xVel[i] = 0;
				yVel[i] = 0;

			}

			if (yPos[i] - circleRadius < 0 || yPos[i] + circleRadius > height) // �ж�С���Ƿ�����
			{
				if (yPos[i] - circleRadius < 0) {
					yPos[i] = circleRadius;
				} else {
					yPos[i] = height - circleRadius;
				}
				yVel[i] *= -1;
			}
			if (xPos[i] - circleRadius < 0 || xPos[i] + circleRadius > width) {
				if (xPos[i] - circleRadius < 0) {
					xPos[i] = circleRadius;
				} else {
					xPos[i] = width - circleRadius;
				}
				xVel[i] *= -1;
			}

		}

	}

	public void surfaceCreated(SurfaceHolder holder) {

		Rect surfaceFrame = holder.getSurfaceFrame();

		width = surfaceFrame.width();
		height = surfaceFrame.height();

		Height = getHeight();
		Width = getWidth();
		if (width > height) {
			width = surfaceFrame.height();
			height = surfaceFrame.width();
			Height = getWidth();
			Width = getHeight();
		}

		// d=(width/2-circleRadius)/9;//ÿȦ�ļ�����
		d = (width / 2) / 9;// ÿȦ�ļ�����
		circleRadius = d / 2;

		int n1 = 0;
		int n2 = 0;
		Random rand = new Random();

		for (int i = 0; i < n + 1; i++)// ��ʼ��С���������
		{
			switch (i) {
			case 0: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2 - circleRadius);
				break;
			}
			case 1: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2 - circleRadius);
				break;
			}
			case 2: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2);
				break;
			}
			case 3: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2);
				break;
			}
			case 4: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			case 5: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			case 6: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			case 7: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			}
			xPos[i] = n1;
			yPos[i] = n2;
		}
		XPos[0] = width / 2;
		YPos[0] = height / 2;

		updateThread = new UpdateThread(this);
		updateThread.setRunning(true);
		updateThread.start();

	}

	// }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

		boolean retry = true;

		updateThread.setRunning(false);
		while (retry) {
			try {
				updateThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public static String CheckInOut(int x0, int y0)// �жϴ����Ƿ���Բ����
	{

		if ((y0 > (Height - height)) && (x0 > (Width - width))) {
			for (int i = 1; i < n + 1; i++) {
				if ((x0 - XPos[i]) * (x0 - XPos[i]) + (y0 - YPos[i])
						* (y0 - YPos[i]) - circleRadius * 9 / 4 * circleRadius < 0)
					return CoreMember[i - 1];
			}
			return "IN";
		}
		return "NULL";// ������������ڻ�������Բ���ڣ��������ַ���"NULL";
	}

	public static void RefreshMove(int _n, int[] _score, String[] _member)

	{

		h = 1;
		second = 1;
		shan = 0;
		num1 = 0;
		n = _n;
		// push=0;
		score = _score;
		CoreMember = _member;
	}

	public static void RefreshStop() {
		h = 0;
		second = 0;
	}

	public static void RefreshRadom(int _totalnum, int _recommember) {
		RefreshStop();
		point[_totalnum] = _recommember + 1;
		shan = 1;

	}

	public static int ReturnWait() {
		if (push < 5 * n + 5)
			return 0;
		else
			return 1;
	}

	public static void Push() {
		push = 0;
	}

}
