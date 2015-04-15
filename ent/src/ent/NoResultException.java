package ent;

public class NoResultException extends Exception
{
	public NoResultException()
	{
		System.out.println("Aucun résultat");
	}
}
