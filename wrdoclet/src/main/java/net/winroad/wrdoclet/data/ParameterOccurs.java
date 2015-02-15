package net.winroad.wrdoclet.data;

/*
 * How many times it occurs.
 */
public enum ParameterOccurs {
	//It should be appeared each time.
	REQUIRED,
	//It may not be appeared.
	OPTIONAL,
	//It may not be appeared. But in some cases, depends on other field, 
	//it will be appeared each time when other field equals to specified value.
	DEPENDS
}
