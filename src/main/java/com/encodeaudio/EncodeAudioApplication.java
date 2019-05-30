package com.encodeaudio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import ddf.minim.AudioSample;
import ddf.minim.Minim;
import processing.core.PApplet;

public class EncodeAudioApplication extends PApplet {

	private static final long serialVersionUID = -6002828871595515053L;

	int SAMPLES = 30000;

	Minim minim;
	AudioSample sample;

	public void setup() {
		size(512, 200);

		String file = selectInput("Select audio file to encode.");

		if (file == null) {
			exit();
			return;
		}

		try {
			minim = new Minim(this);
			sample = minim.loadSample(file);

			float[] samples = sample.getChannel(1);
			float maxval = 0;

			for (int i = 0; i < samples.length; i++) {
				if (abs(samples[i]) > maxval)
					maxval = samples[i];
			}

			int start;

			for (start = 0; start < samples.length; start++) {
				if (abs(samples[start]) / maxval > 0.01)
					break;
			}

			String result = "";
			for (int i = start; i < samples.length && i - start < SAMPLES; i++) {
				result += constrain(((int) map(samples[i], -maxval, maxval, 0, 256)), 0, 255) + ", ";
			}

			String path = System.getProperty("user.home") + "/encode_audio_file.txt";

			try {
				File file2 = new File(path);
				FileWriter fileWriter = new FileWriter(file2);
				fileWriter.write(result);
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String msg = String.format("File created in %s", path);

			JOptionPane.showMessageDialog(null, msg, "Success!", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Maybe you didn't pick a valid audio file?\n" + e, "Error!",
					JOptionPane.ERROR_MESSAGE);
		}

		exit();
	}

	public void stop() {
		sample.close();
		minim.stop();
		super.stop();
	}

}
