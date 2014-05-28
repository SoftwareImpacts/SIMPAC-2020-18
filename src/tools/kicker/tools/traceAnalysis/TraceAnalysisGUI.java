/***************************************************************************
 * Copyright 2014 Kicker Project (http://kicker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kicker.tools.traceAnalysis;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import kicker.tools.traceAnalysis.gui.AbstractStep;
import kicker.tools.traceAnalysis.gui.AdditionalOptionsStep;
import kicker.tools.traceAnalysis.gui.FinalStep;
import kicker.tools.traceAnalysis.gui.PlotStep;
import kicker.tools.traceAnalysis.gui.PrintStep;
import kicker.tools.traceAnalysis.gui.WelcomeStep;

/**
 * @author Nils Christian Ehmke
 * 
 * @since 1.9
 */
public class TraceAnalysisGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private final CardLayout mainPanelLayout = new CardLayout();
	private final JPanel mainPanel = new JPanel(this.mainPanelLayout);
	private final JButton previousButton = new JButton("Previous");
	private final JButton nextButton = new JButton("Next");

	private final StartTraceAnalysisActionListener startTraceAnalysisClickListener = new StartTraceAnalysisActionListener();
	private final AbstractStep[] steps = { new WelcomeStep(), new PlotStep(), new PrintStep(), new AdditionalOptionsStep(),
		new FinalStep(this.startTraceAnalysisClickListener), };
	private int currentStepIndex;

	public TraceAnalysisGUI() {
		super("Trace Analysis Tool - GUI");

		this.addAndLayoutComponents();
		this.initializeComponents();
		this.addLogicToComponents();
		this.initializeWindow();
	}

	private void addAndLayoutComponents() {
		final GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets.set(5, 5, 5, 5);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		this.getContentPane().add(this.mainPanel, gridBagConstraints);

		gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints.insets.set(5, 5, 5, 5);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		this.getContentPane().add(this.previousButton, gridBagConstraints);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints.insets.set(5, 5, 5, 5);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		this.getContentPane().add(this.nextButton, gridBagConstraints);
	}

	private void initializeComponents() {
		this.previousButton.setEnabled(false);
	}

	private void addLogicToComponents() {
		this.nextButton.addActionListener(new ActionListener() {

			@Override
			@SuppressWarnings("synthetic-access")
			public void actionPerformed(final ActionEvent arg0) {
				TraceAnalysisGUI.this.nextStep();
			}
		});

		this.previousButton.addActionListener(new ActionListener() {

			@Override
			@SuppressWarnings("synthetic-access")
			public void actionPerformed(final ActionEvent e) {
				TraceAnalysisGUI.this.previousStep();
			}
		});
	}

	private void nextStep() {
		if (this.steps[this.currentStepIndex].isNextStepAllowed()) {
			this.currentStepIndex++;
			this.mainPanelLayout.next(this.mainPanel);
			this.previousButton.setEnabled(true);
			this.nextButton.setEnabled(this.currentStepIndex < (this.steps.length - 1));
		}
	}

	private void previousStep() {
		this.currentStepIndex--;
		this.mainPanelLayout.previous(this.mainPanel);
		this.nextButton.setEnabled(true);
		this.previousButton.setEnabled(this.currentStepIndex > 0);
	}

	private void initializeWindow() {
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		int maxHeight = 1;
		int maxWidth = 1;
		for (final AbstractStep panel : this.steps) {
			this.mainPanel.add(panel, panel.toString());

			maxHeight = Math.max(maxHeight, panel.getPreferredSize().height);
			maxWidth = Math.max(maxWidth, panel.getPreferredSize().width);
		}
		this.setSize(maxWidth, maxHeight);
		this.setLocationRelativeTo(null);
	}

	private void startTraceAnalysis() {
		final Collection<String> parameters = new ArrayList<String>();

		for (final AbstractStep step : this.steps) {
			step.addSelectedTraceAnalysisParameters(parameters);
		}

		this.previousButton.setEnabled(false);

		final Thread thread = new Thread() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				TraceAnalysisTool.mainHelper(parameters.toArray(new String[parameters.size()]), false);
				TraceAnalysisGUI.this.previousButton.setEnabled(true);
			}
		};

		thread.start();
	}

	public static void main(final String[] args) {
		final TraceAnalysisGUI gui = new TraceAnalysisGUI();
		gui.setVisible(true);
	}

	private class StartTraceAnalysisActionListener implements ActionListener {

		public StartTraceAnalysisActionListener() {
			// No code necessary
		}

		@Override
		@SuppressWarnings("synthetic-access")
		public void actionPerformed(final ActionEvent e) {
			TraceAnalysisGUI.this.startTraceAnalysis();
		}

	}
}