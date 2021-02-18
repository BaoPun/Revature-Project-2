package com.project2.demo.DAO;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project2.demo.beans.Answer;
import com.project2.demo.beans.Permission;
import com.project2.demo.beans.Progress;
import com.project2.demo.beans.Question;
import com.project2.demo.beans.Quiz;
import com.project2.demo.beans.User;
import com.project2.demo.beans.UserType;


@Component
@Transactional
public class DBRepoImpl implements DBRepo {
	
	@Autowired
	private SessionFactory sf;
	
	// User
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int addUser(User a) {
		
		Session session = sf.openSession();
		EntityManager em=sf.createEntityManager();
		int id = em.createNativeQuery("SELECT user_id_generator.nextval FROM DUAL").getFirstResult();
		a.setId(id);
		
		//DML statements use transactions
		try {
			session.beginTransaction();
			id = Integer.parseInt(session.save(a).toString());
			session.getTransaction().commit();
		} 
		catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			id = -1;
		} 
		finally {
			em.close();
			session.close();
		}
		
		return id;

	}

	@Override
	public User getUser(int id) {
		EntityManager em=sf.createEntityManager();
		try {
			return em.find(User.class, id);
		} finally {
			em.close();
		}
	}
	
	@Override
	public User getUser(String name) {
		EntityManager em = sf.createEntityManager();
		try {
			TypedQuery<User> tq = em.createQuery("from User WHERE username=?1",User.class);
			return tq.setParameter(1, name).getSingleResult();
		}
		catch(Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUsers() {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from User").getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllTeachers() {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from User where role=:role")
					 .setParameter("role", UserType.TEACHER)
					 .getResultList();
		} finally {
			em.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllStudents(User teacher){
		
		// This goes in every single method
		Session session = sf.openSession();
		List<User> listOfStudents = null;
		
		try {
			// "User" portion MUST match the Java object, NOT the table.
			listOfStudents = session.createQuery("FROM User WHERE teacher = :id AND role = :role")
					.setParameter("id", teacher)
					.setParameter("role", UserType.STUDENT).getResultList();	
		}
		catch(HibernateException e) {
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		
		return listOfStudents;
	}

	@Override
	public boolean updateUser(User change) {
		
		Session session = sf.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(change);
			transaction.commit();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			transaction.rollback();
			return false;
		}
		finally {
			session.close();
		}
	}

	@Override
	public boolean deleteUser(int id) {
		
		// This goes in every single method
		Session session = sf.openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();	
			session.delete(session.get(User.class, id));
			transaction.commit();
			return true;
		}
		catch(HibernateException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		finally {
			session.close();
		}
		
		return false;
	}
	
	// Quiz
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int addQuiz(Quiz a) {

		Session session = sf.openSession();
		
		// First, obtain the next unique id from the quiz id sequence generator
		EntityManager em = sf.createEntityManager();
		int id = em.createNativeQuery("SELECT quiz_id_generator.nextval FROM DUAL").getFirstResult();
		a.setId(id);
		
		//DML statements use transactions
		try {
			session.beginTransaction();
			id = Integer.parseInt(session.save(a).toString());
			session.getTransaction().commit();
		} 
		catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} 
		finally {
			em.close();
			session.close();
		}
		
		return id;
	}

	@Override
	public Quiz getQuiz(int id) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.find(Quiz.class, id);
		} finally {
			em.close();
		}
	}
	
	@Override
	public List<Quiz> getQuizzes(String name) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Quiz WHERE name=?1",Quiz.class).
					setParameter(1, name).getResultList();
		} finally {
			em.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Quiz> getQuizzesFromUser(int id) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Quiz where userid=?1").
					setParameter(1, id).getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Quiz> getAllQuizzes() {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Quiz").getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public boolean updateQuiz(Quiz change) {
		
		Session session = sf.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(change);
			transaction.commit();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			transaction.rollback();
			return false;
		}
		finally {
			session.close();
		}
	}

	@Override
	public boolean deleteQuiz(int id) {
		// This goes in every single method
		Session session = sf.openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();	
			session.delete(session.get(Quiz.class, id));
			transaction.commit();
			return true;
		}
		catch(HibernateException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		finally {
			session.close();
		}
		
		return false;
	
	}
	
		
	
	// Question
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int addQuestion(Question a) {
		Session session = sf.openSession();
		
		// First, obtain the next unique id from the quiz id sequence generator
		EntityManager em = sf.createEntityManager();
		
		//DML statements use transactions
		try {
			int id = em.createNativeQuery("SELECT question_id_generator.nextval FROM DUAL").getFirstResult();
			a.setId(id);
			session.beginTransaction();
			id = Integer.parseInt(session.save(a).toString());
			session.getTransaction().commit();
			return id;
		} 
		catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			throw e;
		} 
		finally {
			em.close();
			session.close();
		}
	}

	@Override
	public Question getQuestion(int id) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.find(Question.class, id);
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Question> getAllQuestions() {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Question").getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public boolean updateQuestion(Question change) {
		Session session = sf.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(change);
			transaction.commit();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			transaction.rollback();
			return false;
		}
		finally {
			session.close();
		}
	}

	@Override
	public boolean deleteQuestion(int id) {
		// This goes in every single method
		Session session = sf.openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();	
			session.delete(session.get(Question.class, id));
			transaction.commit();
			return true;
		}
		catch(HibernateException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		finally {
			session.close();
		}
		
		return false;
	}
	
	
	// Answer
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int addAnswer(Answer a) {
		Session session = sf.openSession();
		
		EntityManager em = sf.createEntityManager();
		// First, obtain the next unique id from the quiz id sequence generator
		
		//DML statements use transactions
		try {
			int id = em.createNativeQuery("SELECT answer_id_generator.nextval FROM DUAL").getFirstResult();
			a.setId(id);
			session.beginTransaction();
			id = Integer.parseInt(session.save(a).toString());
			session.getTransaction().commit();
			return id;
		} 
		catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			throw e;
		} 
		finally {
			session.close();
		}
		
		
	}

	@Override
	public Answer getAnswer(int id) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.find(Answer.class, id);
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Answer> getAllAnswers(int id) {
		// Get all Answers registered by a particular user id
		Session session = sf.openSession();
		List<Answer> listOfAnswers = null;
		
		try {
			// "User" portion MUST match the Java object, NOT the table.
			// :id is simply a ? variant
			listOfAnswers = session.createQuery("FROM Answer WHERE questionId = ?1")
					.setParameter(1, id).getResultList();
		}
		catch(HibernateException e) {
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return listOfAnswers;
		
	}

	@Override
	public boolean updateAnswer(Answer change) {
		Session session = sf.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(change);
			transaction.commit();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			transaction.rollback();
			return false;
		}
		finally {
			session.close();
		}
	}

	@Override
	public boolean deleteAnswer(int id) {
		// This goes in every single method
		Session session = sf.openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();	
			session.delete(session.get(Answer.class, id));
			transaction.commit();
			return true;
		}
		catch(HibernateException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		finally {
			session.close();
		}
		
		return false;
	
	}
	
	// Progress
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int addProgress(Progress a) {
		Session session = sf.openSession();
		EntityManager em = sf.createEntityManager();
		
		//DML statements use transactions
		try {
			int id = sf.createEntityManager().createNativeQuery("SELECT progress_id_generator.nextval FROM DUAL").getFirstResult();
			a.setId(id);
			session.beginTransaction();
			id = Integer.parseInt(session.save(a).toString());
			session.getTransaction().commit();
			return id;
		} 
		catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			throw e;
		} 
		finally {
			session.close();
			em.close();
		}
	}

	@Override
	public Progress getProgress(int id) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.find(Progress.class, id);
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Progress> getAllProgress(int id) {
		// This goes in every single method
		Session session = sf.openSession();
		List<Progress> listOfProgress = null;
		
		try {
			// "User" portion MUST match the Java object, NOT the table.
			// :id is simply a ? variant
			listOfProgress = session.createQuery("FROM Progress WHERE userId = ?1")
					.setParameter(1, id).getResultList();
		}
		catch(HibernateException e) {
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return listOfProgress;

	}

	@Override
	public boolean updateProgress(Progress change) {
		Session session = sf.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(change);
			transaction.commit();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			transaction.rollback();
			return false;
		}
		finally {
			session.close();
		}
	}

	@Override
	public boolean deleteProgress(int id) {
		// This goes in every single method
		Session session = sf.openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();	
			session.delete(session.get(Progress.class, id));
			transaction.commit();
			return true;
		}
		catch(HibernateException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		finally {
			session.close();
		}
		
		return false;
	
	}
	
	
	
	
	// Permission
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int addPermission(Permission a) {
		Session session = sf.openSession();
		
		EntityManager em = sf.createEntityManager();
		
		//DML statements use transactions
		try {
			int id = em.createNativeQuery("SELECT permission_id_generator.nextval FROM DUAL").getFirstResult();
			a.setId(id);
			session.beginTransaction();
			id = Integer.parseInt(session.save(a).toString());
			session.getTransaction().commit();
			return id;
		} 
		catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			throw e;
		} 
		finally {
			session.close();
			em.close();
		}
		

	}

	@Override
	public Permission getPermission(int id) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.find(Permission.class, id);
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Permission> getAllPermissions() {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Permission").getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Permission> getAllPermissions(int id) {
		// Get all Permissions by user id
		Session session = sf.openSession();
		List<Permission> listOfPermissions = null;
		
		try {
			// "User" portion MUST match the Java object, NOT the table.
			// :id is simply a ? variant
			listOfPermissions = session.createQuery("FROM Permission WHERE userId = ?1")
					.setParameter(1, id).getResultList();
		}
		catch(HibernateException e) {
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return listOfPermissions;
	}

	@Override
	public boolean updatePermission(Permission change) {
		Session session = sf.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(change);
			transaction.commit();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			transaction.rollback();
			return false;
		}
		finally {
			session.close();
		}
	}

	@Override
	public boolean deletePermission(int id) {
		// This goes in every single method
		Session session = sf.openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();	
			session.delete(session.get(Permission.class, id));
			transaction.commit();
			return true;
		}
		catch(HibernateException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		finally {
			session.close();
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Progress> getProgressForUserAndQuiz(int quizid, int userid) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Progress p where p.answer.question.quiz.id=?1 and p.user.id=?2 order by p.answer.ordering asc").
					  setParameter(1, quizid).
					  setParameter(2, userid).
					  getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Question> getQuizQuestions(int quizid) {
		EntityManager em = sf.createEntityManager();
		try {
			return em.createQuery("from Question q where q.quiz.id=?1 order by q.ordering").
					 setParameter(1, quizid).
					 getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Answer> getQuestionAnswers(int questionid) {
		EntityManager em = sf.createEntityManager();
		try {
			return  em.createQuery("from Answer a where a.question.id=?1 order by a.ordering").
					setParameter(1, questionid).
					getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUserStudents(int teacherid) {
		EntityManager em = sf.createEntityManager();
		try {
			return  em.createQuery("from User u where u.teacher.id=?1").
					setParameter(1, teacherid).
					getResultList();
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Quiz> getQuizzesStartedByStudent(int studentid) {
		EntityManager em = sf.createEntityManager();
		try {
			return  em.createQuery("select distinct p.answer.question.quiz from Progress p where p.user.id=?1").
					setParameter(1, studentid).
					getResultList();
		} finally {
			em.close();
		}
	}
	
	
	
	

}
