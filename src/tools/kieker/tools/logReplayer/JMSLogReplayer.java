/***************************************************************************
 * Copyright 2011 by
 *  + Christian-Albrechts-University of Kiel
 *    + Department of Computer Science
 *      + Software Engineering Group 
 *  and others.
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

package kieker.tools.logReplayer;

import java.util.Collection;

import kieker.analysis.AnalysisController;
import kieker.analysis.plugin.IMonitoringRecordConsumerPlugin;
import kieker.analysis.reader.IMonitoringReader;
import kieker.analysis.reader.JMSReader;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.IMonitoringRecordReceiver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Listens to a JMS queue and simply passes each record to a specified {@link IMonitoringRecordReceiver}.
 * 
 * @author Andre van Hoorn
 */
public class JMSLogReplayer {

	private static final Log LOG = LogFactory.getLog(JMSLogReplayer.class);
	/** Each record is delegated to this receiver. */
	private final IMonitoringRecordReceiver recordReceiver;

	private final String jmsProviderUrl;
	private final String jmsDestination;
	private final String jmsFactoryLookupName;

	/** Must not be used for construction */
	@SuppressWarnings("unused")
	private JMSLogReplayer() {
		this(null, null, null, null);
	}

	/**
	 * @param jmsProviderUrl
	 *            = for instance "tcp://127.0.0.1:3035/"
	 * @param jmsDestination
	 *            = for instance "queue1"
	 * @param jmsFactoryLookupName
	 *            = for instance "org.exolab.jms.jndi.InitialContextFactory" (OpenJMS)
	 * @throws IllegalArgumentException
	 *             if passed parameters are null or empty.
	 */
	public JMSLogReplayer(final IMonitoringRecordReceiver recordReceiver, final String jmsProviderUrl, final String jmsDestination, final String jmsFactoryLookupName) {
		this.recordReceiver = recordReceiver;
		this.jmsProviderUrl = jmsProviderUrl;
		this.jmsDestination = jmsDestination;
		this.jmsFactoryLookupName = jmsFactoryLookupName;
	}

	/**
	 * Replays the monitoring log terminates after the last record was passed to
	 * the configured {@link IMonitoringRecordReceiver}.
	 * 
	 * @return true on success; false otherwise
	 */
	public boolean replay() {
		boolean success = true;

		final IMonitoringReader logReader = new JMSReader(this.jmsProviderUrl, this.jmsDestination, this.jmsFactoryLookupName);
		final AnalysisController tpanInstance = new AnalysisController();
		tpanInstance.setReader(logReader);
		tpanInstance.registerPlugin(new RecordDelegationPlugin2(this.recordReceiver));
		try {
			tpanInstance.run();
			success = true;
		} catch (final Exception ex) {
			JMSLogReplayer.LOG.error("Exception", ex);
			success = false;
		}
		return success;
	}
}

/**
 * Kieker analysis plugin that delegates each record to the configured {@link IMonitoringRecordReceiver}.
 * 
 * TODO: We need to extract this class and merge it with that of {@link FilesystemLogReplayer} See ticket http://samoa.informatik.uni-kiel.de:8000/kieker/ticket/173
 * 
 * @author Andre van Hoorn
 * 
 */
class RecordDelegationPlugin2 implements IMonitoringRecordConsumerPlugin {

	private static final Log LOG = LogFactory.getLog(RecordDelegationPlugin.class);

	private final IMonitoringRecordReceiver rec;

	/**
	 * Must not be used for construction.
	 */
	@SuppressWarnings("unused")
	private RecordDelegationPlugin2() {
		this(null);
	}

	public RecordDelegationPlugin2(final IMonitoringRecordReceiver rec) {
		this.rec = rec;
	}

	/*
	 * {@inheritdoc}
	 */
	@Override
	public boolean newMonitoringRecord(final IMonitoringRecord record) {
		return this.rec.newMonitoringRecord(record);
	}

	/*
	 * {@inheritdoc}
	 */
	@Override
	public boolean execute() {
		RecordDelegationPlugin2.LOG.info(RecordDelegationPlugin.class.getName() + " starting ...");
		return true;
	}

	/*
	 * {@inheritdoc}
	 */
	@Override
	public void terminate(final boolean error) {
		return;
	}

	/*
	 * {@inheritdoc}
	 */
	@Override
	public Collection<Class<? extends IMonitoringRecord>> getRecordTypeSubscriptionList() {
		return null; // receive records of any type
	}
}
