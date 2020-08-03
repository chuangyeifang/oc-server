package com.oc.core;

import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.manager.ClusterManager;
import com.oc.cluster.node.NodeID;
import com.oc.cluster.task.ClusterMessageRouter;
import com.oc.core.server.BsServer;
import com.oc.core.server.Server;
import com.oc.dispatcher.AllotDispatcher;
import com.oc.dispatcher.Dispatcher;
import com.oc.routing.RoutingTable;
import com.oc.routing.RoutingTableImpl;
import com.oc.scheduler.CancelableScheduler;
import com.oc.scheduler.HashedWheelTimeoutScheduler;
import com.oc.utils.ClusterExternalizableUtil;
import com.oc.utils.DummyExternalizableUtil;
import com.oc.utils.ExternalizableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月30日
 * @version v 1.0
 */
@SuppressWarnings("unused")
public class OcImServer {
	private static Logger log = LoggerFactory.getLogger(OcImServer.class);
	private NodeID nodeId;
	private static OcImServer instance;
	private static final NodeID DEFAULT_NODE_ID = new NodeID(new byte[0]);

	private HazelcastInstance hazelcastInstance;

	private RoutingTable routingTable;
	
	private Dispatcher dispatcher;
	
	private CancelableScheduler scheduler;
	
	private ClusterMessageRouter clusterMessageRouter;
	
	public static OcImServer getInst() {
		return instance;
	}
	
	public OcImServer() {
		this(null);
	}
	
	public OcImServer(HazelcastInstance hazelcastInstance) {
		if (null != instance) {
			throw new IllegalStateException("已经存在一个OCServer正在运行中...");
		}
		instance = this;
		this.hazelcastInstance = hazelcastInstance;
	}

	public NodeID getNodeId() {
		return nodeId == null ? DEFAULT_NODE_ID : nodeId;
	}

	public void setNodeId(NodeID nodeId) {
		this.nodeId = nodeId;
	}
	
	public RoutingTable getRoutingTable() {
		return routingTable;
	}
	
	public Dispatcher getDispatcher() {
		return dispatcher;
	}
	
	public CancelableScheduler getScheduler() {
		return scheduler;
	}
	
	public ClusterMessageRouter getClusterMessageRouter() {
		return clusterMessageRouter;
	}
	
	public void startCluster(boolean isCluster) {
		
		if (isCluster && null != hazelcastInstance) {
			ExternalizableUtil.getInstance().setStrategy(new ClusterExternalizableUtil());
			ClusterManager clusterManager = new ClusterManager(hazelcastInstance);
			nodeId = clusterManager.startCluster();
		} else {
			ExternalizableUtil.getInstance().setStrategy(new DummyExternalizableUtil());
		}
		
		this.routingTable = new RoutingTableImpl();
		this.dispatcher = new AllotDispatcher();
		this.scheduler = new HashedWheelTimeoutScheduler();
		this.clusterMessageRouter = new ClusterMessageRouter();

		ExecutorService bsEs = new ThreadPoolExecutor(
				1,
				1,
				30L,
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(2),
				r -> {
					Thread thread = new Thread(r);
					thread.setName("OCIM-BS-Thread-V1");
					return thread;
				});
		bsEs.execute(new ServerStart(new BsServer()));

		// TODO 暂不启动
		//  ExecutorService bsEs = new ThreadPoolExecutor(
		//		1,
		//		1,
		//		30L,
		//		TimeUnit.SECONDS,
		//		new ArrayBlockingQueue<>(2),
		//		r -> {
		//			Thread thread = new Thread(r);
		//			thread.setName("OCIM-CS-Thread-V1");
		//			return thread;
		//		});
		//	bsEs.execute(new ServerStart(new CsServer()));
	}
	
	/**
	 * 后台启动
	 */
	private static class ServerStart implements Runnable {
		private final Server server;

		public ServerStart(Server server) {
			this.server = server;
		}

		@Override
		public void run() {
			server.start();
		}
	}
}
