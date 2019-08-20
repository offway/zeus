package cn.offway.zeus.config;



import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;

/**
 * Models a <tt>BETWEEN</tt> {@link Predicate}.
 *
 * @author Steve Ebersole
 */
public class DiscountPredicate<Y>
		extends AbstractSimplePredicate
		implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Expression<? extends Y> expression;
	private final Expression<? extends Y> expression1;
	private final Expression<? extends Y> object;

	public DiscountPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<? extends Y> expression,
			Expression<? extends Y> expression1,
			Y object) {
		this(
				criteriaBuilder,
				expression,
				expression1,
				criteriaBuilder.literal( object )
		);
	}

	public DiscountPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<? extends Y> expression,
			Expression<? extends Y> expression1,
			Expression<? extends Y> object) {
		super( criteriaBuilder );
		this.expression = expression;
		this.expression1 = expression1;
		this.object = object;
	}

	public Expression<? extends Y> getExpression() {
		return expression;
	}

	public Expression<? extends Y> getExpression1() {
		return expression1;
	}

	public Expression<? extends Y> getObject() {
		return object;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		Helper.possibleParameter( getExpression(), registry );
		Helper.possibleParameter( getExpression1(), registry );
		Helper.possibleParameter( getObject(), registry );
	}

	@Override
	public String render(boolean isNegated, RenderingContext renderingContext) {
		return ( (Renderable) getExpression() ).render( renderingContext )
				+"*"+
				( (Renderable) getObject() ).render( renderingContext )
				+ " <= "
				+ ( (Renderable) getExpression1() ).render( renderingContext );
	}


}
