# Style Guidelines

## Ruby

The [Ruby Style Guide](https://github.com/bbatsov/ruby-style-guide) has some pretty
great recommendations, but here is where we differ a bit:

### When to use `{..}` vs `do..end`

Follow [The Weirich Rule][2] which, summarised, is

* Use `{..}` for blocks that return values
* Use `do..end` for blocks that are executed for side effects

### When to use parenthesis around methods calls

Ruby is pretty cool as it doesn't always force you to use parenthesis, but
sometimes this could make the code ambiguous. Here are a few tips that aren't
mentioned in the Github guide:

```ruby
some_method
some_method() # Don't do it this way though, too java-ish :)
some_method_with_args 1, 2, hello: '123'
some_method_with_args(1, 2, hello: '123') # Use whatever looks best to you
some_method_with_args 1, 2, {
  hello: '123',
  world: '456'
}
some_method_with_args(1, 2, {
  hello: '123',
  world: '456'
})
another_object.some_method oi: 'mundo', hello: 'world'
```

But once the method call appears within an assignment, the parenthesis should
be used when there are arguments:

```ruby
@foo = @bar.baz # No parenthesis required
@foo = bar # Still no parenthesis
@converter = options.fetch :converter, EI::ZendeskContactConverter.new # Now it needs parenthesis
@converter = options.fetch(:converter, EI::ZendeskContactConverter.new) # Now it's not as ambiguous
```

Another case where they should be included is when a method call without
parenthesis has "already been used", so if you call another method, it needs to
use parenthesis if it has arguments:

```ruby
foo bar # Assuming that bar is a method call, doesn't need parenthesis
obj.should eq '123' # Needs parenthesis
obj.should eq('123') # Easier to read, not as ambiguous
```

### Usage of spaces with Hashes vs Blocks

Follow the idea of Jim Weirich's Rule and allowing code to express your intent,
in order to make it clear straight away when a block is being used or if it's
data structure (Hash or Array), use the follow conventions:

```ruby
{ telephones: { '$in' => contact.telephones } } # This kinda looks like a block
{telephones: {'$in' => contact.telephones}} # This is definately not a block
{telephones: {'$in' => [telephone1, telephone2]}} # Same thing with array [] so its consistent with hashes
foo { bar } # This is definitely a method call that's passing a block
foo {bar: 1} # This is definitely a method call that's received a hash (although this should be foo bar: 1)
```

### Yardoc

If you need formal documentation for the code, use [Yardoc][1] rather than Tomdoc.

---

[Go back to the README](../README.md) or [go back to the Development Practices](development_practices.md)

[1]: http://rubydoc.info/docs/yard/file/docs/Tags.md
[2]: http://onestepback.org/index.cgi/Tech/Ruby/BraceVsDoEnd.rdoc
