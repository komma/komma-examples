package net.enilink.komma.example.behaviour;

import java.util.Arrays;

import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IEntityManagerFactory;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.core.URI;
import net.enilink.komma.core.URIs;
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
import net.enilink.komma.example.behaviour.runtime.ActivityInstanceSupport;
import net.enilink.komma.example.behaviour.runtime.EndInstanceSupport;
import net.enilink.komma.example.behaviour.runtime.ProcessDefinitionSupport;
import net.enilink.komma.example.behaviour.runtime.ProcessInstanceSupport;
import net.enilink.komma.example.behaviour.runtime.TaskDefinitionSupport;
import net.enilink.komma.example.behaviour.runtime.TaskInstanceSupport;
import net.enilink.komma.example.behaviour.runtime.TransitionSupport;
import net.enilink.komma.example.objectmapping.util.ExampleModule;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	final static String URI_STRING = "urn:enilink.net:komma:behaviour-example#";
	final static URI bexNS = URIs.createURI(URI_STRING);

	private static void demonstrateProcess(IEntityManager manager) {
		// create a process
		ProcessDefinition processDef = manager.createNamed(
				bexNS.appendFragment("Process1"), ProcessDefinition.class);
		processDef.setName("DMC-12 Testrun");

		// add four activities: start, task1, task2, end
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

		System.out.println("process "
				+ processInstance.getURI()
				+ " is now in: state="
				+ processInstance.getState()
				+ " activity='"
				+ processInstance.getCurrentActivity().getUsesDefinition()
						.getName() + "'");

		// the process should now be waiting for Doc to fall, hit his head on
		// the sink and make his famous invention
		ActivityInstance ai1 = processInstance.getCurrentActivity();
		if (!(ai1 instanceof TaskInstance)
				|| !ai1.getUsesDefinition().getName().equals(task1.getName())
				|| !IProcessInstance.STATE_ACTIVE.equals(processInstance
						.getState())) {
			System.err
					.println("**** Oops! Process is not in expected state (expected: state="
							+ IProcessInstance.STATE_ACTIVE
							+ " task='"
							+ task1.getName() + "')! ****");
		} else {
			TaskInstance invent = (TaskInstance) ai1;
			System.out.println("complete()ing current task: name='"
					+ invent.getUsesDefinition().getName() + "', for='"
					+ invent.getUsesDefinition().getForUser() + "', state="
					+ invent.getState() + "...");

			// It is now 1955; Doc did it!
			((ITaskInstance) invent).complete(null);
		}

		// we need to refresh our bean
		processInstance.refresh();
		System.out.println("process "
				+ processInstance.getURI()
				+ " is now in: state="
				+ processInstance.getState()
				+ " activity='"
				+ processInstance.getCurrentActivity().getUsesDefinition()
						.getName() + "'");

		// process should now be waiting for Einstein to experience the first
		// time travel in history, on the Twin Pikes Mall's parking lot
		ActivityInstance ai2 = processInstance.getCurrentActivity();
		if (!(ai2 instanceof TaskInstance)
				|| !ai2.getUsesDefinition().getName().equals(task2.getName())
				|| !IProcessInstance.STATE_ACTIVE.equals(processInstance
						.getState())) {
			System.err
					.println("**** Oops! Process is not in expected state (expected: state="
							+ IProcessInstance.STATE_ACTIVE
							+ " task='"
							+ task2.getName() + "')! ****");
		} else {
			TaskInstance experience = (TaskInstance) ai2;
			System.out.println("complete()ing current task: name='"
					+ experience.getUsesDefinition().getName() + "', for='"
					+ experience.getUsesDefinition().getForUser() + "', state="
					+ experience.getState() + "...");

			// It is now 1985; Einstein just skipped 1 minute into the future!
			((ITaskInstance) experience).complete(null);
		}

		// we need to refresh our bean
		processInstance.refresh();
		System.out.println("process "
				+ processInstance.getURI()
				+ " is now in: state="
				+ processInstance.getState()
				+ " activity='"
				+ processInstance.getCurrentActivity().getUsesDefinition()
						.getName() + "'");

		ActivityInstance aiEnd = processInstance.getCurrentActivity();
		if (!(aiEnd instanceof ActivityInstance)
				|| !aiEnd.getUsesDefinition().getName().equals(end.getName())
				|| !IProcessInstance.STATE_ENDED.equals(processInstance
						.getState())) {
			System.err
					.println("**** Oops! Process is not in expected state (expected: state="
							+ IProcessInstance.STATE_ENDED
							+ " activity='"
							+ end.getName() + "')! ****");
		} else {
			System.out.println("**** Success! Process '" + processDef.getName()
					+ "' has completed! ****");
		}

		System.out.println("the following activities have been created:");
		for (ActivityInstance ai : manager.findAll(ActivityInstance.class)
				.toList()) {
			System.out.println(ai.getURI() + " name='"
					+ ai.getUsesDefinition().getName() + "' state="
					+ ai.getState() + " transition=" + ai.getTransitionName());
		}
	}

	public static void main(String[] args) throws RepositoryException {

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
						addBehaviour(TaskDefinitionSupport.class);
						addBehaviour(TaskInstanceSupport.class);
						addBehaviour(EndInstanceSupport.class);
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
