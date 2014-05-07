package hha.main;

import hha.aiml.Chat;
import hha.util.music.Player;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.parser.AliceBotParser;

import com.example.robottest.R;
import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechUnderstander;
import com.iflytek.speech.SpeechUnderstanderListener;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.UnderstanderResult;
import com.robot.util.SystemUiHider;

public class MainActivity extends Activity {

	private Toast mToast;
	private TextView text = null;
	private SystemUiHider mSystemUiHider;

	private Button mainButton = null;
	private Button button = null;
	private SpeechUnderstander su = null;

	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	private InitListener il = new InitListener() {

		@SuppressLint("NewApi")
		@Override
		public void onInit(ISpeechModule arg0, int code) {
			// TODO Auto-generated method stub
			if (code == ErrorCode.SUCCESS) {
				button.setEnabled(true);
				if (bot != null) {
					String ansString = bot.respond("welcome");
					// ansString = Chat.chineseTranslate(ansString);
					text.setText(ansString + "\n");
					reader.start(ansString);

				}
			}
		}

	};

	private void showTip(final String str) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}

	private SpeechUnderstanderListener sul = new SpeechUnderstanderListener.Stub() {

		@Override
		public void onVolumeChanged(int v) throws RemoteException {
			showTip("onVolumeChanged:" + v);
		}

		@Override
		public void onError(int errorCode) throws RemoteException {
			showTip("onError Code:" + errorCode);
		}

		@Override
		public void onEndOfSpeech() throws RemoteException {
			showTip("onEndOfSpeech");
		}

		@Override
		public void onBeginOfSpeech() throws RemoteException {
			showTip("onBeginOfSpeech");
		}

		private void playmusic(Data data) {
			String music_url = null;
			String music_title = data.name != null ? data.name : "";
			String music_singer = data.singer != null ? data.singer : "";
			try {
				music_url = hha.util.music.GetMusicUrl.getMusic(music_title,
						music_singer);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			player.playUrl(music_url);
		}

		private void Show(String userString, String ansString) {
			text.setText(text.getText() + "\n用户:" + userString + '\n');
			text.setText(text.getText() + ansString + "\n");
			audiom.setStreamVolume(AudioManager.STREAM_MUSIC, oldAudio,
					AudioManager.FLAG_PLAY_SOUND);
			reader.start(ansString);
		}

		@Override
		public void onResult(final UnderstanderResult result)
				throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {

						SaxParseService saxParseService = new SaxParseService();
						Data data = null;
						try {
							String string = result.getResultString();
							data = saxParseService.getData(string);
							// text.setText(text.getText()+string+"\n");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// text.setText(text.getText()+"content:"+data.content+"\n");
						// text.setText(text.getText()+"focus:"+data.focus+"\n");
						// text.setText(text.getText()+"topic:"+data.topic+"\n");
						// text.setText(text.getText()+"parsedtext:"+data.parsedtext+"\n");
						try {
							if ((data.focus != null)
									&& ("music".equals(data.focus))) {
								playmusic(data);
								String ansString = bot.respond("OK");
								Show(data.rawtext, ansString);
								return;
							}

						} catch (Exception e) {
							// TODO: handle exception
						}

						String input = data.rawtext;
						input = Chat.chineseTranslate(input);
						text.setText(text.getText() +"分词:"+ input );
						if (Chat.END.equalsIgnoreCase(input))
							return;
						String ansString = null;
						if (bot != null) {
							boolean cannotfind = false;

							if (input == null)
								cannotfind = true;
							else {

								ansString = bot.respond(input);

								if (data.content != null) {
									String output = "";
									String command = null;
									bitoflife.chatterbean.Context context = (bot != null ? bot
											.getContext() : null);
									if (context != null) {
										output = (String) context
												.property("predicate.CanNotFind");
										context.property(
												"predicate.CanNotFind", "null");
										command = (String) context
												.property("predicate.Command");
										context.property("predicate.Command",
												"null");
									}

									if ("True".equals(output))
										cannotfind = true;
									else
										cannotfind = false;
									// text.setText(text.getText()+"\n"+output);

									if ("Stop".equals(command)) {
										player.stop();
									}

								}
							}

							if (cannotfind)
								ansString = data.content;

							Show(data.rawtext, ansString);
							cannotfind = false;
						}
					} else {
						text.setText("解析失败");
					}
				}
			});
		}
	};

	private AliceBot bot;
	private Reader reader;

	public String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder sb = new StringBuilder();

		String line = null;

		try {

			while ((line = reader.readLine()) != null) {

				sb.append(line + "/n");

			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				is.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}

	private ByteArrayOutputStream gossip;
	AssetManager am;
	Player player;
	AudioManager audiom;
	int oldAudio;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		// Chat.init();

		setContentView(R.layout.maininterface);
		mainButton = (Button) findViewById(R.id.button_main);
		mainButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, MenuList.class);
				MainActivity.this.startActivity(intent);
			}
		});

		audiom = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		text = (TextView) findViewById(R.id.main_text);
		button = (Button) findViewById(R.id.button_speak);
		button.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN)// 判断按钮释放被释�?
				{
					setParam();
					oldAudio = audiom
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					audiom.setStreamVolume(AudioManager.STREAM_MUSIC, 1,
							AudioManager.FLAG_PLAY_SOUND);
					int code = su.startUnderstanding(sul);
					if (code != 0) {
						text.setText("Error Code: " + code);
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP)// 判断按钮释放被释�?
				{

					int code = su.stopUnderstanding(sul);
					if (code != 0) {
						text.setText("Error Code: " + code);
					}
				}
				return false;
			}
		});
		if (SpeechUtility.getUtility(this).queryAvailableEngines() != null
				&& SpeechUtility.getUtility(this).queryAvailableEngines().length > 0) {
			text.setText("讯飞语音可以使用");
		}

		SpeechUtility.getUtility(MainActivity.this).setAppid("53227870");

		// sr = new SpeechRecognizer(this,il);
		// sr.setParameter(SpeechConstant.PARAMS, "asr_ptt=1");

		su = new SpeechUnderstander(this, il);
		this.mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

		Context context = getApplicationContext();
		reader = new Reader(context);
		try {
			am = getAssets();
			
			///初始化分词系统
			InputStream prop = am.open("jcseg.properties",AssetManager.ACCESS_BUFFER);
			Chat.init(prop);
			prop.close();
			
			String[] strings = null;
			strings = am.list("lexicon");
	
			//InputStream[] inputStreams = new InputStream[strings.length];
			for (int i = 0; i < strings.length; i++) {
				InputStream is= am.open("lexicon/" + strings[i],AssetManager.ACCESS_BUFFER);
				Chat.initDic(is);
				is.close();
			}
			Chat.initSeg();
			///初始化机器人
			gossip = new ByteArrayOutputStream();

			AliceBotParser parser = new AliceBotParser();

			bot = parser.parse(am.open("context.xml",AssetManager.ACCESS_BUFFER),
					am.open("splitters.xml",AssetManager.ACCESS_BUFFER), am.open("substitutions.xml",AssetManager.ACCESS_BUFFER),
					am.open("idiom.aiml",AssetManager.ACCESS_BUFFER));

			bitoflife.chatterbean.Context bot_context = bot.getContext();
			bot_context.outputStream(gossip);

			// /初始化Player
			player = new Player(new SeekBar(context));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			text.setText(e.toString());
		}
		// text.setText(bot.respond("welcome"));

	}

	private void setParam() {
		// TODO Auto-generated method stub
		su.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
		su.setParameter(SpeechConstant.LANGUAGE, "zh-cn");
		su.setParameter(SpeechConstant.VAD_BOS, "4000");
		su.setParameter(SpeechConstant.VAD_EOS, "1000");

		String param = "asr_ptt=0";
		su.setParameter(SpeechConstant.PARAMS, param);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}