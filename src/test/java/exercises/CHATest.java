package exercises;

import analysis.CallGraph;
import analysis.exercise1.CHAAlgorithm;
import base.TestSetup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transformer;
import soot.jimple.parser.node.TGoto;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Targets;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CHATest extends TestSetup {

	private Scene scene;
	private CallGraph cg;
	private SootMethod exampleMain;
	private SootMethod exampleConstructor;
	private SootMethod subjectConstructor;
	private SootMethod exampleSubjectModify;
	private SootMethod observerableNotifyObservers;
	private SootMethod observerableNotifyObserversSpecific;
	private SootMethod observerUpdate;
	private SootMethod exampleUpdate;


	protected Transformer createAnalysisTransformer() {
		return new SceneTransformer() {
			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				//Scene.v().getApplicationClasses().stream().forEach(c -> System.out.println(c.toString()));
				//Scene.v().getEntryPoints().stream().forEach(c -> System.out.println(c.toString()));

				scene = Scene.v();
			}
		};

	}

	@Before
	public void setUp() throws Exception {
		executeStaticAnalysis();
		exampleMain = scene.getMethod("<target.exercise1.SimpleExample: void main(java.lang.String[])>");
		exampleConstructor = scene.getMethod("<target.exercise1.SimpleExample: void <init>()>");
		subjectConstructor = scene.getMethod("<target.exercise1.SimpleExample$Subject: void <init>()>");
		exampleSubjectModify = scene.getMethod("<target.exercise1.SimpleExample$Subject: void modify()>");
		observerableNotifyObservers = scene.getMethod("<target.exercise1.Observable: void notifyObservers()>");
		observerableNotifyObserversSpecific = scene.getMethod("<target.exercise1.Observable: void notifyObservers(java.lang.Object)>");
		observerUpdate = scene.getMethod("<target.exercise1.Observer: void update(target.exercise1.Observable,java.lang.Object)>");
		exampleUpdate = scene.getMethod("<target.exercise1.SimpleExample: void update(target.exercise1.Observable,java.lang.Object)>");
		
//		CHATransformer.v().transform();
////		SootClass a = scene.getSootClass("testers.A")
////		SootMethod src = Scene.v().getMainClass().getMethodByName("doStuff");
//		soot.jimple.toolkits.callgraph.CallGraph cg2 = Scene.v().getCallGraph();
//		
//		serializeCallGraph(cg2, "output");
//		System.out.println("serializeGraph completed...!");
		
//		Iterator<MethodOrMethodContext> targets = new Targets(cg2.edgesOutOf(exampleMain));
//		while(targets.hasNext()) {
//			SootMethod tgt = (SootMethod) targets.next();
//			System.out.println(exampleMain + " may call " + tgt);
//		}
		
		CHAAlgorithm cha = new CHAAlgorithm();
		cg = cha.constructCallGraph(scene);
	}

	@Test public void constructorCalls() {
		// static to constructor call
		Set<SootMethod> calledFromMain = cg.edgesOutOf(exampleMain);
//		System.out.println("calledFromMain size: " + calledFromMain.size());
//		System.out.println("exampleMain size: " + exampleMain.getSignature() + exampleMain.getDeclaration());
//		Iterator<SootMethod> iterator = calledFromMain.iterator();
//		while(iterator.hasNext()) {
//			SootMethod edge = (SootMethod) iterator.next();
//			System.out.println("declaration " + edge.getDeclaration());
//		}
		
//		soot.jimple.toolkits.callgraph.CallGraph cg2 = 
//		Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(exampleMain));
		Assert.assertTrue(calledFromMain.contains(exampleConstructor));
		Assert.assertTrue(calledFromMain.contains(subjectConstructor));
	}

	@Test public void staticToInstanceCall() {
		// static to instance call
		Set<SootMethod> calledFromMain = cg.edgesOutOf(exampleMain);
		Assert.assertTrue(calledFromMain.contains(exampleSubjectModify));
	}

	@Test public void instanceToInterfaceMethod() {
		// instance to interface method
		Set<SootMethod> calledFromModify = cg.edgesOutOf(exampleSubjectModify);
		Assert.assertTrue(calledFromModify.contains(observerableNotifyObservers));

		// more specific
		Set<SootMethod> calledFromNotify = cg.edgesOutOf(observerableNotifyObservers);
		Assert.assertTrue(calledFromNotify.contains(observerableNotifyObserversSpecific));
	}

	@Test
	public void polymorphicCallSite() {
		// polymorphic call site (interface)
		Set<SootMethod> calledMethods = cg.edgesOutOf(observerableNotifyObserversSpecific);
		Assert.assertTrue(calledMethods.contains(observerUpdate));
		Assert.assertTrue(calledMethods.contains(exampleUpdate));
	}
	
	private void serializeCallGraph(soot.jimple.toolkits.callgraph.CallGraph graph, String fileName) {
		if (fileName == null) {
	        fileName = soot.SourceLocator.v().getOutputDir();
	        if (fileName.length() > 0) {
	            fileName = fileName + java.io.File.separator;
	        }
	        fileName = fileName + "call-graph" + DotGraph.DOT_EXTENSION;
	    }		
//		System.out.println("writing to file " + fileName);
		DotGraph canvas = new DotGraph("call-graph");
		QueueReader<Edge> listener = graph.listener();
		while (listener.hasNext()) {
			Edge next = listener.next();
			
			MethodOrMethodContext src= next.getSrc();
			MethodOrMethodContext tgt = next.getTgt();
			String srcString = src.toString();
			String tgtString = tgt.toString();
			if( (!srcString.startsWith("<java.") && 
					!srcString.startsWith("<sun.") && 
					!srcString.startsWith("<org.") &&
					!srcString.startsWith("<com.") &&
					!srcString.startsWith("<jdk") && 
					!srcString.startsWith("<javax.")) 
					&& 
					(!tgtString.startsWith("<java.") &&
							!tgtString.startsWith("<sun.") &&
							!tgtString.startsWith("<org.") &&
							!tgtString.startsWith("<com.") &&
							!tgtString.startsWith("<jdk.") &&
							!tgtString.startsWith("<javax.")
							)) {
				canvas.drawNode(src.toString());
				canvas.drawNode(tgt.toString());
				canvas.drawEdge(src.toString(), tgt.toString());
//				System.out.println("src = " + src.toString());
//				System.out.println("tgt = " + tgt.toString());
				
			}
			
		}
		canvas.plot(fileName);
//		System.out.println("Returning");
		return;
		
	}

}
