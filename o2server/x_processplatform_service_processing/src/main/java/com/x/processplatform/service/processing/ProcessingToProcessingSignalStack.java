package com.x.processplatform.service.processing;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.x.processplatform.core.entity.log.SignalStack;

public class ProcessingToProcessingSignalStack extends ConcurrentHashMap<String, SignalStack> {

	private static final String SPLIT = "#";

	private static final long serialVersionUID = 5924230440506195407L;

	public SignalStack open(String work, String series, SignalStack signalStack) {
		return this.put(key(work, series), signalStack);
	}

	public Optional<SignalStack> find(String work, String series) {
		return Optional.ofNullable(this.get(key(work, series)));
	}

	public void close(String work, String series) {
		this.remove(key(work, series));
	}

	private String key(String work, String series) {
		return work + SPLIT + series;
	}

//	@Test
//	public void test() {
//		ProcessingToProcessingSignalMapping m = new ProcessingToProcessingSignalMapping();
//		Signal s1 = m.open("111", "222");
//		System.out.println(s1);
//		Signal s2 = m.open("111", "222");
//		System.out.println(s2);
//		System.out.println(s2==s1);		
//		m.close("111", "222");
//		m.close("111", "222");
//		System.out.println(m.size());
//	}

}
