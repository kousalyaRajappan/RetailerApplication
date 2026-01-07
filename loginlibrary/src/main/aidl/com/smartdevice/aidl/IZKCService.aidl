package com.smartdevice.aidl;

import com.smartdevice.aidl.ICallBack;
import android.graphics.Bitmap;
import android.content.Context;

interface IZKCService
{

	boolean setModuleFlag(int module);

	void registerCallBack(String flag,ICallBack callback);

	void unregisterCallBack(String flag,ICallBack callback);

	void stopRunningTask();

	boolean isTaskRunning();
	void getFirmwareVersion();
	String getServiceVersion();

	int getDeviceModel();

	void printerInit();

	String getPrinterStatus();

	void printerSelfChecking();

	boolean checkPrinterAvailable();

	void sendRAWData(String flag,in byte[] data);

    boolean setPrinterLanguage(String lang, int langCode);

	void setAlignment(int alignment);

	void setTypeface(int type);

	void setFontSize(int type);

	void printGBKText(String text);

	void printUnicodeText(String text);
	void printTextWithFont(String text, int type, int size);

	void printTextAlgin(String text, int type, int size, int alginStyle);

	void printColumnsText(in String[] colsTextArr, in int[] colsWidthArr, in int[] colsAlign);

	void printBitmap(in Bitmap bitmap);
	void printBitmapAlgin(in Bitmap bitmap, int width, int height,int position);

	Bitmap createBarCode(String data, int codeFormat, int width, int height, boolean displayText);

	Bitmap createQRCode(String data, int width, int height);

	void generateSpace();

	boolean printImageGray(in Bitmap bitmap);

	boolean printRasterImage(in Bitmap bitmap);

	boolean printUnicode_1F30(String textStr);

	void setPrintLanguage(String language);
	String getFirmwareVersion1();

	String getFirmwareVersion2();

	 void openBackLight(int btFlg);


	 boolean showRGB565Image(in Bitmap bitmapSrc);


	 boolean showRGB565ImageByPath(String path);

	 boolean showRGB565ImageLocation(in Bitmap bitmapSrc,int x, int y, int width, int height);

	 boolean updateLogo(in Bitmap bitmapSrc);

	 boolean updateLogoByPath(String path);


	 boolean showDotImage(int BackColor, int ForeColor,in Bitmap bitmapSrc);


	 boolean showRGB565ImageCenter(in Bitmap bitmapSrc);


	int Open();

	int Close(long fd);


	boolean setGPIO(int io,int status);


	int openCard(int carPositin);


	int openCard2(inout int[] fd,int slotno);

	int openCard3(long fd,int slotno);
	int CloseCard();


	int CloseCard2(long fd, boolean v2);

	byte[] ResetCard(int power);



	int ResetCard2(long fd, inout byte[] atr,inout int[] atrLen);

	byte[] ResetCard3(long fd,int slotno,int pw);
	byte[] CardApdu(in byte[] apdu);

	int CardApdu2(long fd, in byte[] apdu,int apduLength, inout byte[] response,inout int[] respLength);


	byte[] CardApdu3(long fd, in byte[] apdu,int apduLength);


	void openScan(boolean status);

	void scan();


	void dataAppendEnter(boolean  status);

	void appendRingTone(boolean status);

	void continueScan(boolean status);
	void scanRepeatHint(boolean status);

	void recoveryFactorySet(boolean status);

	byte[] sendCommand(in byte[] buffer);

	boolean isScanning();

	String getIdentifyInfo();
	boolean turnOn();

	boolean turnOff();


	Bitmap getHeader();

}