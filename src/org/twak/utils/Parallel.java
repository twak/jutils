package org.twak.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Parallel<I, O> {

	public interface Work<I, O> {
		public O work( I i );
	}

	public interface Complete<O> {
		public void complete( Set<O> dones );
	}

	public interface WorkFactory<I> {
		public I generateWork();
		public boolean shouldAbort();
	}
	
	
	public static class ListWF<I> implements WorkFactory<I>{

		List<I> list;
		AtomicInteger index;
		boolean abort = false;
		
		public ListWF( List<I> in ) {
			this.list = in;
			index = new AtomicInteger(-1);
		}
		
		@Override
		public I generateWork() {
			int i = index.incrementAndGet();
			if (i >= list.size())
				return null;
			
			System.out.println( i+" parallel jobs remain" );
			
			return list.get( i );
		}

		public void abort() {
			abort = true;
		}
		
		@Override
		public boolean shouldAbort() {
			return abort;
		}
	}
	

//	public Parallel( List<I> in, Work<I, O> work, Complete<O> done, boolean block ) {
//	}


	public Parallel( List<I> lines, Work<I, O> work, Complete<O> done, boolean block ) {
		this ( new ListWF(lines), work, done, block, -1 );
	}
	
	public Parallel( List<I> lines, Work<I, O> work, Complete<O> done, boolean block, int tCount_ ) {
		this ( new ListWF(lines), work, done, block, tCount_ );
	}

	
	public Parallel( WorkFactory<I> gen, Work<I, O> work, Complete<O> done, boolean block, int tCount_ ) {

		int tCount = tCount_ <= 0 ? Runtime.getRuntime().availableProcessors() / 2 : tCount_; // hyperthreads....

		Set<O> os = Collections.synchronizedSet( new HashSet<>() );

		CountDownLatch cdl = new CountDownLatch( tCount );

		for ( int i = 0; i < tCount; i++ ) {
			new Thread() {

				@Override
				public void run() {

					try {
						while ( true ) {

							I next = gen.generateWork();
							
							if ( next == null )
								return;
							
							try {
								work.work( next );
							} catch ( Throwable th ) {
								th.printStackTrace();
							}
							
							System.out.println( "parallel job done" );
							
							if (gen.shouldAbort())
								break;
						}
					} finally {
						cdl.countDown();
					}
				}
			}.start();
		}

		Thread t = new Thread() {
			public void run() {

				try {
					cdl.await();
					if ( done != null )
						done.complete( os );
					System.out.println( "parallel complete done" );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			};
		};

		if ( block )
			t.run();
		else
			t.start();
	}
}
