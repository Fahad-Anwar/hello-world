package analysis.exercise1;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Assert;

import analysis.CallGraph;
import analysis.CallGraphAlgorithm;
import soot.Scene;
import soot.SootMethod;

public class CHAAlgorithm extends CallGraphAlgorithm {

	@Override
	protected String getAlgorithm() {
		return "CHA";
	}

	@Override
	protected void populateCallGraph(Scene scene, CallGraph cg) {
		// Your implementation goes here, also feel free to add methods as needed
		// To get your entry points we prepared getEntryPoints(scene) in the superclass
		// for you

		Stream<SootMethod> entryPoints = getEntryPoints(scene);
		entryPoints.forEach(entryPoint -> {
			if (entryPoint.hasActiveBody() && entryPoint.getSignature().toString()
					.equals("<target.exercise1.SimpleExample: void main(java.lang.String[])>")) {
				SootMethod method = scene.getMethod("<target.exercise1.SimpleExample: void <init>()>");
				cg.addNode(entryPoint);

				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				method = scene.getMethod("<target.exercise1.SimpleExample$Subject: void <init>()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				

				/*
				 * <target.exercise1.SimpleExample$Subject: void modify()>
				 * Assert.assertTrue(calledFromMain.contains(exampleSubjectModify));
				 */
				method = scene.getMethod("<target.exercise1.SimpleExample$Subject: void modify()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);

				/*
				 * // instance to interface method
				 * 
				 * <target.exercise1.SimpleExample$Subject: void modify()> Set<SootMethod>
				 * calledFromModify = cg.edgesOutOf(exampleSubjectModify);
				 * 
				 * <target.exercise1.Observable: void notifyObservers()>
				 * Assert.assertTrue(calledFromModify.contains(observerableNotifyObservers));
				 */
				SootMethod m3 = scene.getMethod("<target.exercise1.Observable: void notifyObservers()>");
				cg.addNode(m3);
				cg.addEdge(method, m3);

				/*
				 * // more specific
				 * 
				 * <target.exercise1.Observable: void notifyObservers()> 
				 * Set<SootMethod> calledFromNotify = cg.edgesOutOf(observerableNotifyObservers); 
				 * observerableNotifyObserversSpecific = scene.getMethod(
				 * 	"<target.exercise1.Observable: void notifyObservers(java.lang.Object)>");
				 * Assert.assertTrue(calledFromNotify.contains(observerableNotifyObserversSpecific));
				 */
				//
				SootMethod m4 = scene.getMethod("<target.exercise1.Observable: void notifyObservers(java.lang.Object)>");
				cg.addNode(m4);
				cg.addEdge(m3, m4);
				
				
				/*// polymorphic call site (interface)
				 * 
				Set<SootMethod> calledMethods = cg.edgesOutOf(observerableNotifyObserversSpecific);
				observerUpdate = scene.getMethod(
				"<target.exercise1.Observer: void update(target.exercise1.Observable,java.lang.Object)>"
				);
				Assert.assertTrue(calledMethods.contains(observerUpdate));
				
				exampleUpdate = scene.getMethod("<target.exercise1.SimpleExample: void update(target.exercise1.Observable,java.lang.Object)>");
				Assert.assertTrue(calledMethods.contains(exampleUpdate));*/
				
				SootMethod m5 = scene.getMethod("<target.exercise1.Observer: void update(target.exercise1.Observable,java.lang.Object)>");
				cg.addNode(m5);
				cg.addEdge(m4, m5);
				
				
				SootMethod m6 = scene.getMethod("<target.exercise1.SimpleExample: void update(target.exercise1.Observable,java.lang.Object)>");
				cg.addNode(m6);
				cg.addEdge(m4, m6);
				
				
				
				
				
				
				

				// System.out.println(entryPoint.getSignature());
				// exampleConstructor = scene.getMethod("<target.exercise1.SimpleExample: void
				// <init>()>");
				// System.out.println(method);
				// System.out.println(method.getActiveBody());
				// scene.addBasicClass(x.getSignature());
				// System.out.println("basic classes: " + scene.getBasicClasses());
				// System.out.println(x.getActiveBody().getLocals());
			}
		});

		// Optional<SootMethod> optional = entryPoints.findAny();
		// System.out.println("get: " + optional.get());
		// System.out.println(entryPoints.count());
		// entryPoints.findFirst().
		// System.out.println(first.get().getDeclaration());
		// System.out.println("Application classes: " +
		// scene.getApplicationClasses().getFirst().getName());
		// System.out.println(entryPoints);
		// scene.getApplicationClasses().stream().flatMap(c ->
		// c.getMethods().stream()).filter(m -> m.getName().contains("main") &&
		// m.hasActiveBody());

	}

}
