package com.sensei.search.client.json.req.query;

import java.util.List;

import com.sensei.search.client.json.CustomJsonHandler;
import com.sensei.search.client.json.JsonField;
import com.sensei.search.client.json.req.Term;
/**
 * <p>A query that generates the union of documents produced by its subqueries, and that scores each document with the maximum score for that document as produced by any subquery, plus a tie breaking increment for any additional matching subqueries.</p>
<p>This is useful when searching for a word in multiple fields with different boost factors (so that the fields cannot be combined equivalently into a single search field).  We want the primary score to be the one associated with the highest boost, not the sum of the field scores (as Boolean Query would give). If the query is “albino elephant” this ensures that “albino” matching one field and “elephant” matching another gets a higher score than “albino” matching both fields. To get this result, use both Boolean Query and DisjunctionMax Query: for each term a DisjunctionMaxQuery searches for it in each field, while the set of these DisjunctionMaxQuery’s is combined into a BooleanQuery.</p>
<p>The tie breaker capability allows results that include the same term in multiple fields to be judged better than results that include this term in only the best of those multiple fields, without confusing this with the better case of two different terms in the multiple fields.The default <code>tie_breaker</code> is <code>0.0</code>.</p>
<p>This query maps to Sensei <code>DisjunctionMaxQuery</code>.</p>

<pre class="prettyprint lang-js"><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; </span><span class="str">"dis_max"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; </span><span class="str">"tie_breaker"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="lit">0.7</span><span class="pun">,</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; </span><span class="str">"boost"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="lit">1.2</span><span class="pun">,</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; </span><span class="str">"queries"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="pun">[</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span><span class="str">"term"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="pun">{</span><span class="pln"> </span><span class="str">"age"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="lit">34</span><span class="pln"> </span><span class="pun">}</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span><span class="pun">},</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span><span class="str">"term"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="pun">{</span><span class="pln"> </span><span class="str">"age"</span><span class="pln"> </span><span class="pun">:</span><span class="pln"> </span><span class="lit">35</span><span class="pln"> </span><span class="pun">}</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span><span class="pun">}</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; </span><span class="pun">]</span><span class="pln"><br>&nbsp; &nbsp; </span><span class="pun">}</span><span class="pln"><br></span><span class="pun">}</span><span class="pln"> &nbsp; &nbsp;</span>
 *
 */
@CustomJsonHandler(QueryJsonHandler.class)
public class DisMax implements Query {
    @JsonField("tie_braker")
    private double tieBraker;
    private double boost;
    private List<Term> queries;
    public DisMax(double tieBraker, List<Term> queries, double boost) {
        super();
        this.tieBraker = tieBraker;
        this.boost = boost;
        this.queries = queries;
    }

}
