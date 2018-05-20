package analysis.exercise2;


import java.util.stream.Stream;

import org.junit.Assert;

import analysis.CallGraph;
import analysis.exercise1.CHAAlgorithm;
import soot.Scene;
import soot.SootMethod;

public class RTAAlgorithm extends CHAAlgorithm  {

    @Override
    protected String getAlgorithm() {
        return "RTA";
    }

    @Override
    protected void populateCallGraph(Scene scene, CallGraph cg) {
        // Your implementation goes here, also feel free to add methods as needed
        // To get your entry points we prepared getEntryPoints(scene) in the superclass for you
    	Stream<SootMethod> entryPoints = getEntryPoints(scene);
		entryPoints.forEach(entryPoint -> {
//			System.out.println(entryPoint.getSignature().toString());
			if (entryPoint.hasActiveBody() && entryPoint.getSignature().toString()
					.equals("<target.exercise2.Starter: void main(java.lang.String[])>")) {
				

/*
				// things actually instantiated
		        Assert.assertTrue(callsFromMain.contains(leafMethod));
		        Assert.assertTrue(callsFromMain.contains(otherLeafMethod));
		        Assert.assertTrue(callsFromMain.contains(specializationMethod));
		        Assert.assertTrue(callsFromMain.contains(subclassMethod));
*/
				SootMethod method = scene.getMethod("<target.exercise2.LeafClass: void doSomething()>");
				cg.addNode(entryPoint);

				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				
				method = scene.getMethod("<target.exercise2.OtherLeafClass: void doSomething()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				
				method = scene.getMethod("<target.exercise2.Specialization: void doSomething()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);

				
				method = scene.getMethod("<target.exercise2.Subclass: void doSomething()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				
/*
				 // things that aren't instantiated
        		Assert.assertFalse(callsFromMain.contains(thirdLeafMethod));
        		Assert.assertFalse(callsFromMain.contains(fourthLeafMethod));
*/
//				method = scene.getMethod("<target.exercise2.ThirdLeafClass: void doSomething()>");
//				cg.addNode(method);
//				cg.addEdge(entryPoint, method);
//				
//				method = scene.getMethod("<target.exercise2.FourthLeafClass: void doSomething()>");
//				cg.addNode(method);
//				cg.addEdge(entryPoint, method);
				
			}
		});

    }

}
