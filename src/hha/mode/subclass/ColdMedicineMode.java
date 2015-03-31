package hha.mode.subclass;

import java.util.ArrayList;

import android.R.anim;
import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;
import android.app.Activity;



import com.lilele.automatic.AuTomatic;

import hha.main.MainActivity;
import hha.medicinecold.MedicineColdActivity;
import hha.mode.Mode;

public class ColdMedicineMode extends Mode{

	static int pos_question;
	String[] symbols={"start","nose","fever","snot","cough"};
	
	public ColdMedicineMode(MainActivity mainActivity, AuTomatic auto) {
		super(mainActivity, auto);
		i_waitTime = (int) N_rand(10, 5, 5, 20);
		pos_question=0;
	}

	@Override
	protected String getModeName() {
		// TODO Auto-generated method stub
		return "cold";
	}

	@Override
	public void Run(int UserCount, int RobotCount) {
		// TODO Auto-generated method stub
				if ((i_waitTime == RobotCount)||(6 == UserCount)) {
					mainActivity.ShowTextOnUIThread("CheckItem");
					boolean iscold_end=CheckItem();
					if (iscold_end) {
						String ans = bot.Respond("cold_end");
			        	mainActivity.Show(null, ans);
			        	Log.i("propety", bot.getProperty("symptom_cold_nose")+"_"+bot.getProperty("symptom_cold_sneeze")+"_"+bot.getProperty("symptom_cold_snot"));
			        	String symptom_cold_nose = bot.getProperty("symptom_cold_nose");
			        	String symptom_cold_sneeze=bot.getProperty("symptom_cold_sneeze");
			            String symptom_cold_fever = bot.getProperty("symptom_cold_fever");
			            String symptom_cold_snot=bot.getProperty("symptom_cold_snot");
			            String symptom_cold_cough=bot.getProperty("symptom_cold_cough");
//			          Intent intent = new Intent(this.mainActivity,MedicineColdActivity.class);
//			            Bundle bundle_cold =new Bundle();
			            String cold_symptoms = "";
			            if (symptom_cold_nose!=null && symptom_cold_nose.equals("true")) {
//			            	  bundle_cold.putBoolean("symptom_cold_nose", true);
			            	  cold_symptoms+="true_";
						}else {
//							  bundle_cold.putBoolean("symptom_cold_nose", false);
							  cold_symptoms+="false_";
						}
			            
//			            if (symptom_cold_sneeze!=null && symptom_cold_sneeze.equals("true")) {
////			            	  bundle_cold.putBoolean("symptom_cold_sneeze", true);
//			            	  cold_symptoms+="true_";
//						}else {
////							 bundle_cold.putBoolean("symptom_cold_sneeze", false);
//							 cold_symptoms+="false_";
//						}
			            
			            if (symptom_cold_fever!=null && symptom_cold_fever.equals("true")) {
//			            	  bundle_cold.putBoolean("symptom_cold_fever", true);
			            	  cold_symptoms+="true_";
						}else {
//							  bundle_cold.putBoolean("symptom_cold_fever", false);
							  cold_symptoms+="false_";
						}
			            
			            if (symptom_cold_snot!=null && symptom_cold_snot.equals("true")) {
//			            	  bundle_cold.putBoolean("symptom_cold_snot", true);
			            	  cold_symptoms+="true_";
						}else {
//							 bundle_cold.putBoolean("symptom_cold_snot", false);
							 cold_symptoms+="false_";
						}
			            
			            if (symptom_cold_cough!=null && symptom_cold_cough.equals("true")) {
	//            	  bundle_cold.putBoolean("symptom_cold_sneeze", true);
		            	  cold_symptoms+="true";
				      }else {
///					 bundle_cold.putBoolean("symptom_cold_sneeze", false);
						 cold_symptoms+="false";
					}
			          
			        	bot.setProperty("mode", "normal");
			        	bot.setProperty("cold_symptoms", cold_symptoms);
//			        	intent.putExtra("bundle_cold",bundle_cold);
//			        	this.mainActivity.startActivity(intent);
//			        	try {
//			        		   this.mainActivity.startActivity(intent);
//						} catch (Exception e) {
//						      Log.i("error", e.getMessage());
//						}
//			     
			        	
			        
			        	
					}

					i_waitTime = (int) N_rand(30, 6, 25, 35);
				}
				if (UserCount == 90) {
					mainActivity.ShowTextOnUIThread("免打扰");
					mainActivity.Show(null, bot.Respond("AUTO_确认听见"));
				}
				
				if (UserCount == 180) {
					mainActivity.ShowTextOnUIThread("免打扰");
					mainActivity.Show(null, bot.Respond("AUTO_免打扰"));
					auto.setB_exit(true);
				}
		
	}

	private boolean CheckItem() {
	        if (pos_question>=5) {
	        	pos_question=0;
	        	
				return true;
			}else {
				String ans = bot.Respond("cold_" + symbols[pos_question]);
				pos_question++;
				mainActivity.Show(null, ans);
				return false;
			}
			
		
	}

}
