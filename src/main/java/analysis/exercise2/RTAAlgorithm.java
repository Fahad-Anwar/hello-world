package analysis.exercise2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import analysis.CallGraph;
import analysis.exercise1.CHAAlgorithm;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class RTAAlgorithm extends CHAAlgorithm {

	@Override
	protected String getAlgorithm() {
		return "RTA";
	}

	@Override
	protected void populateCallGraph(Scene scene, CallGraph cg) {
		// Your implementation goes here, also feel free to add methods as needed
		// To get your entry points we prepared getEntryPoints(scene) in the superclass
		// for you
		Stream<SootMethod> entryPoints = getEntryPoints(scene);
		entryPoints.forEach(entryPoint -> {
			
			if (entryPoint.toString().equals("<target.exercise2.Starter: void main(java.lang.String[])>")) {
				// test(entryPoint);
				Map<SootMethod, ArrayList<SootClass>> map = new HashMap<>();
				checkMethod(null, entryPoint, cg, scene, map);
			}
		});

	}

	private void checkMethod(SootMethod src, SootMethod entryPoint, CallGraph cg, Scene scene, Map<SootMethod, ArrayList<SootClass>> map) {
		// TODO Auto-generated method stub
		try {
			
			if (!cg.hasNode(entryPoint)) {
				cg.addNode(entryPoint);
			}

			if (src != null && !cg.hasNode(src)) {
				cg.addNode(src);
			}

			if (src != null && !cg.hasEdge(src, entryPoint)) {
				cg.addEdge(src, entryPoint);
//				System.out.println(src + " -> " + entryPoint);
			}
			
			if (entryPoint.hasActiveBody()) {
				Body b = entryPoint.getActiveBody();
				for (Unit u : b.getUnits()) {

					Stmt s = (Stmt) u;
					if (s.containsInvokeExpr()) {
						InvokeExpr expr = s.getInvokeExpr();
//						System.out.println("\tExp: " + expr + " Method: " + expr.getMethod() );
						checkMethod(entryPoint, expr.getMethod(), cg, scene, map);
						if(expr.getMethod().isStatic() && !expr.getMethod().isJavaLibraryMethod()) {
							ArrayList<SootClass> classes;
							if(map.containsKey(entryPoint)) {
								classes = map.get(entryPoint);
							} else {
								classes = new ArrayList<SootClass>();
								map.put(entryPoint, classes);
								//list.put(entryPoint, value);
							}
							cg.edgesOutOf(expr.getMethod()).stream().filter(e -> !e.isJavaLibraryMethod()).forEach( m -> {
								classes.add(m.getDeclaringClass());
							});
									
						}
					}

				}

			} else if (entryPoint.isAbstract() && !entryPoint.isJavaLibraryMethod()) {
				ArrayList<SootClass> list =	map.containsKey(src) ? map.get(src) : new ArrayList<>();
				scene.getApplicationClasses().stream().
				flatMap(c -> c.getMethods().stream()).
				filter(method -> method.getSubSignature().equals(entryPoint.getSubSignature())						
						&& method.getDeclaringClass() != entryPoint.getDeclaringClass()).forEach(m -> {
							if(list.contains(m.getDeclaringClass())) {
								addEdge(src, m, cg);
							}
						});
				
				 
				
//				scene.getApplicationClasses().forEach(sampleClass -> {
//					sampleClass.getMethods().forEach(method -> {
//						if (method.getSubSignature().equals(entryPoint.getSubSignature()) && 
//								method.getDeclaringClass() != entryPoint.getDeclaringClass()) {
//							System.out.println(method + "   -> " + entryPoint + " src= " + src
//									);
//							if(method.hasActiveBody()) {
//								method.getActiveBody().getUnits().forEach(box -> {
//									Stmt s = (Stmt) box;
//									if (s.containsInvokeExpr()) {
//										System.out.println("expr " + s.getInvokeExpr());
//									}
//								});
//							}
//							//checkMethod(entryPoint, method, cg, scene);
//							// addToSubClasses(entryPoint, method, scene);
//						}
//					});
//				});

			}

			

		} catch (Exception e) {
			System.out.println("EXCEPTION:");
		} finally {
		}
	}

	private void addEdge(SootMethod src, SootMethod m, CallGraph cg) {
		if(!cg.hasNode(src)) cg.addNode(src);
		if(!cg.hasNode(m)) cg.addNode(m);
		if(!cg.hasEdge(src, m)) cg.addEdge(src, m);
		
	}

//	private void addToSubClasses(SootMethod methodFromWhereItsCalled, SootMethod abstractMethod, Scene scene) {
//		// Original caller(source) is our (entryPoint)
//		scene.getApplicationClasses().forEach(sampleClass -> {
//			
//			if(doesExtends(sampleClass, abstractMethod)) {
//				//System.out.println("returned true for " + sampleClass);
//			}
//			
//		});
//		if (methodFromWhereItsCalled.getDeclaringClass().hasSuperclass()) {
//			if (!methodFromWhereItsCalled.getDeclaringClass().getSuperclass().isJavaLibraryClass()) {
//				SootClass sClass = methodFromWhereItsCalled.getDeclaringClass().getSuperclass();
//				System.out.println(methodFromWhereItsCalled.getDeclaringClass() + "        extends     " + sClass.getName());
//				sClass.getMethods().forEach(method -> {
//					System.out.println("     Method: " + method);
//				});
//				
//				methodFromWhereItsCalled.getDeclaringClass().getMethods().forEach(method -> {
//					System.out.println("     Metho2: " + method);
//				});
//			}
//		}
//		
//	}

	 
//	private boolean doesExtends(SootClass nextClass, SootMethod abstractMethod) {
//		// TODO Auto-generated method stub
//		//System.out.println(sampleClass  + " == " + targetClass + " -> " + (sampleClass==targetClass));
//		boolean foundmethod = false;
//		if(nextClass.getName() != abstractMethod.getDeclaringClass().getName()) {
//			List<SootMethod> methods = nextClass.getMethods();
//			for (int i = 0; i < methods.size(); i++) {
//				if (!methods.get(i).isAbstract() && methods.get(i).getName().equals(abstractMethod.getName())) {
//					System.out.println(methods.get(i));
//					foundmethod = true;
//				}
//			}
//		}
//		if(!foundmethod && nextClass.hasSuperclass() && !nextClass.getSuperclass().isJavaLibraryClass()) {
//			doesExtends(nextClass.getSuperclass(), abstractMethod);			
//		}		
//		return foundmethod;
//	}
//		
//
//	private void test(SootMethod entryPoint) {
//		if (!entryPoint.isJavaLibraryMethod()) {
//			System.out.println("abstarct: " + entryPoint + "     Declaring Class: " + entryPoint.getDeclaringClass());
//		}
//		if (entryPoint.hasActiveBody() && entryPoint.isConcrete() && !entryPoint.isJavaLibraryMethod()) {
//
//			// System.out.println(entryPoint);
//			Body b = entryPoint.getActiveBody();
//			for (Unit u : b.getUnits()) {
//
//				Stmt s = (Stmt) u;
//				if (s.containsInvokeExpr()) {
//
//					InvokeExpr expr = s.getInvokeExpr();
//					test(expr.getMethod());
//					// System.out.println(entryPoint + " -> " + expr.getMethod());
//				}
//
//			}
//
//		} else if (entryPoint.isAbstract() && !entryPoint.isJavaLibraryMethod()) {
//			// System.out.println("abstarct: " + entryPoint + " Declaring Class: " +
//			// entryPoint.getDeclaringClass());
//		}
//
//	}

}
