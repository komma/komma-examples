package net.enilink.komma.example.behaviour;

import java.util.Arrays;

import javax.xml.datatype.DatatypeConfigurationException;

import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IEntityManagerFactory;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.core.URI;
import net.enilink.komma.core.URIImpl;
import net.enilink.komma.example.behaviour.model.ActivityDefinition;
import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.DefinitionAspect;
import net.enilink.komma.example.behaviour.model.ProcessDefinition;
import net.enilink.komma.example.behaviour.model.ProcessInstance;
import net.enilink.komma.example.behaviour.model.RuntimeAspect;
import net.enilink.komma.example.behaviour.model.TaskDefinition;
import net.enilink.komma.example.behaviour.model.TaskInstance;
import net.enilink.komma.example.behaviour.model.Transition;
import net.enilink.komma.example.behaviour.runtime.ActivityDefinitionSupport;
import net.enilink.komma.example.behaviour.runtime.ActivityExecutionSupport;
import net.enilink.komma.example.behaviour.runtime.ActivityInstanceSupport;
import net.enilink.komma.example.behaviour.runtime.EndExecutionSupport;
import net.enilink.komma.example.behaviour.runtime.ProcessDefinitionSupport;
import net.enilink.komma.example.behaviour.runtime.ProcessInstanceSupport;
import net.enilink.komma.example.behaviour.runtime.TaskDefinitionSupport;
import net.enilink.komma.example.behaviour.runtime.TaskExecutionSupport;
import net.enilink.komma.example.behaviour.runtime.TransitionSupport;
import net.enilink.komma.example.objectmapping.util.ExampleModule;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	final static String URI_STRING = "urn:enilink.net:komma:behaviour-example#";
	final static URI bexNS = URIImpl.createURI(URI_STRING);

	private static void demonstrateProcess(IEntityManager manager) {
		// create a process
		ProcessDefinition processDef = manager.createNamed(
				bexNS.appendFragment("Process1"), ProcessDefinition.class);
		processDef.setName("DMC-12 Testrun");

		// add three activities: start, task, end
		ActivityDefinition start = manager
				.createNamed(bexNS.appendFragment("ActivityStart"),
						ActivityDefinition.class);
		start.setName("start");
		TaskDefinition task1 = manager.createNamed(
				bexNS.appendFragment("ActivityTask1"), TaskDefinition.class);
		task1.setName("Invent Flux Capacitor");
		task1.setForUser("Emmett Lathrop 'Doc' Brown");
		TaskDefinition task2 = manager.createNamed(
				bexNS.appendFragment("ActivityTask2"), TaskDefinition.class);
		task2.setName("Experience Time Travel");
		task2.setForUser("Einstein");
		ActivityDefinition end = manager.createNamed(
				bexNS.appendFragment("ActivityEnd"), ActivityDefinition.class);
		end.setName("end");

		// add transitions from start to task1 to task2 to end
		Transition s_t1 = manager.create(Transition.class);
		s_t1.setName("toTask1");
		s_t1.setToActivity(task1);
		Transition t1_t2 = manager.create(Transition.class);
		t1_t2.setName("toTask2");
		t1_t2.setToActivity(task2);
		Transition t2_e = manager.create(Transition.class);
		t2_e.setName("toEnd");
		t2_e.setToActivity(end);

		start.getHasTransition().add(s_t1);
		task1.getHasTransition().add(t1_t2);
		task2.getHasTransition().add(t2_e);

		// add the activities to the process
		processDef.getHasActivity().addAll(
				Arrays.asList(start, task1, task2, end));

		// create and start a new instance of the process
		ProcessInstance processInstance = ((IProcessDefinition) processDef)
				.createProcessInstance();

		System.out.println("process " + processInstance.getURI()
				+ " is now in state: " + processInstance.getState());

		// the process should now be waiting for Doc to fall, hit his head on
		// the sink and make his famous invention
		ActivityInstance ai1 = processInstance.getCurrentActivity();
		if (!(ai1 instanceof TaskInstance)) {
			System.err.println("Oops! Process is not in expected state!");
		}
		TaskInstance invent = (TaskInstance) ai1;
		System.out.println("current task: name='"
				+ invent.getUsesDefinition().getName() + "', for='"
				+ invent.getUsesDefinition().getForUser() + "', state="
				+ invent.getState());

		// It is now 1955 and Doc did it!
		((ITaskExecution) invent.getUsesDefinition()).complete(invent, null);

		// we need to refresh our bean
		processInstance.refresh();
		System.out.println("process " + processInstance.getURI()
				+ " is now in state: " + processInstance.getState());

		// process should now be waiting for Einstein to experience the first
		// time travel in history, on the Twin Pikes Mall's parking lot
		ActivityInstance ai2 = processInstance.getCurrentActivity();
		if (!(ai2 instanceof TaskInstance)) {
			System.err.println("Oops! Process is not in expected state!");
		}
		TaskInstance experience = (TaskInstance) ai2;
		System.out.println("current task: name='"
				+ experience.getUsesDefinition().getName() + "', for='"
				+ experience.getUsesDefinition().getForUser() + "', state="
				+ experience.getState());

		// It is now 1985 and Einstein just skipped 1 minute into the future!
		((ITaskExecution) experience.getUsesDefinition()).complete(experience,
				null);

		// we need to refresh our bean
		processInstance.refresh();
		System.out.println("process " + processInstance.getURI()
				+ " is now in state: " + processInstance.getState());

		System.out.println("the following activities have been created:");
		for (ActivityInstance ai : manager.findAll(ActivityInstance.class)
				.toList()) {
			System.out.println(ai.getURI() + " name='"
					+ ai.getUsesDefinition().getName() + "' state="
					+ ai.getState() + " transition=" + ai.getTransitionName());
		}
	}

	public static void main(String[] args)
			throws DatatypeConfigurationException, RepositoryException {

		// create a sesame repository
		SailRepository dataRepository = new SailRepository(new MemoryStore());
		dataRepository.initialize();

		// create an entity manager and register concepts
		IEntityManager manager = createEntityManager(new ExampleModule(
				dataRepository, new KommaModule() {
					{
						// model classes, definition
						addConcept(DefinitionAspect.class);
						addConcept(ProcessDefinition.class);
						addConcept(ActivityDefinition.class);
						addConcept(TaskDefinition.class);
						addConcept(Transition.class);
						// model classes, runtime
						addConcept(RuntimeAspect.class);
						addConcept(ProcessInstance.class);
						addConcept(ActivityInstance.class);
						addConcept(TaskInstance.class);

						// behaviour classes
						addBehaviour(ProcessDefinitionSupport.class);
						addBehaviour(ProcessInstanceSupport.class);
						addBehaviour(ActivityDefinitionSupport.class);
						addBehaviour(ActivityInstanceSupport.class);
						addBehaviour(ActivityExecutionSupport.class);
						addBehaviour(TaskDefinitionSupport.class);
						addBehaviour(TaskExecutionSupport.class);
						addBehaviour(EndExecutionSupport.class);
						addBehaviour(TransitionSupport.class);
					}
				}));

		demonstrateProcess(manager);
	}

	private static IEntityManager createEntityManager(ExampleModule module) {
		Injector injector = Guice.createInjector(module);
		IEntityManagerFactory factory = injector
				.getInstance(IEntityManagerFactory.class);
		IEntityManager manager = factory.get();
		return manager;
	}
}
