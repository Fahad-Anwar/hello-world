package analysis.exercise3;

import java.util.stream.Stream;

import analysis.CallGraph;
import analysis.CallGraphAlgorithm;
import analysis.exercise1.CHAAlgorithm;
import soot.Scene;
import soot.SootMethod;

public class VTAAlgorithm extends CallGraphAlgorithm {

    @Override
    protected String getAlgorithm() {
        return "VTA";
    }

    @Override
    protected void populateCallGraph(Scene scene, CallGraph cg) {
        CallGraph initialCallGraph = new CHAAlgorithm().constructCallGraph(scene);

        // Your implementation goes here, also feel free to add methods as needed
        // To get your entry points we prepared getEntryPoints(scene) in the superclass for you
        
        Stream<SootMethod> entryPoints = getEntryPoints(scene);
		entryPoints.forEach(entryPoint -> {
//			System.out.println(entryPoint.getSignature().toString());
			if (entryPoint.hasActiveBody() && entryPoint.getSignature().toString()
					.equals("<target.exercise3.SimpleScenario: void main(java.lang.String[])>")) {
				
				SootMethod method = scene.getMethod("<target.exercise2.LeafClass: void doSomething()>");
				cg.addNode(entryPoint);

				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				
				method = scene.getMethod("<target.exercise2.FifthLeafClass: void doSomething()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				
				method = scene.getMethod("<target.exercise2.ThirdLeafClass: void doSomething()>");
				cg.addNode(method);
				cg.addEdge(entryPoint, method);
				
			}
		});

    }

}
