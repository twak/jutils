package org.twak.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Parallel <I,O> {

	public interface Work<I,O> {
		public O work (I i );
	}
	
	public interface Complete<O> {
		public void complete (Set<O> dones);
	}

	public Parallel (List<I> in, Work<I,O> work, Complete<O> done, boolean block) {
		
		BlockingQueue<I> togo = new ArrayBlockingQueue<>( in.size() );
		
		togo.addAll( in );
		
		int tCount = Runtime.getRuntime().availableProcessors() / 2 /*hyperthreads are shite*/;
		
		Set<O> os = Collections.synchronizedSet( new HashSet<>() );
		
		CountDownLatch cdl = new CountDownLatch( tCount );
		
		for ( int i = 0; i < tCount; i++ ) {
			new Thread() {

				@Override
				public void run() {

					try {
						while ( true ) {
							I i = togo.poll();

							System.out.println( "jobs remaining: " + togo.size() );

							if ( i == null )
								return;

							os.add( work.work( i ) );
							System.out.println( "job done" );

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
					done.complete( os );
					System.out.println( "done" );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			};
		};

		if (block)
			t.run();
		else
			t.start();
		
	}
	
}
