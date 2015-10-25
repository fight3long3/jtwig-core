package org.jtwig.environment;

import org.jtwig.context.model.EscapeMode;
import org.jtwig.functions.JtwigFunction;
import org.jtwig.model.expression.operation.binary.BinaryOperator;
import org.jtwig.model.expression.operation.unary.UnaryOperator;
import org.jtwig.parser.JtwigParserConfiguration;
import org.jtwig.parser.addon.AddonParserProvider;
import org.jtwig.parser.cache.NoTemplateCacheProvider;
import org.jtwig.parser.cache.TemplateCacheProvider;
import org.jtwig.resource.resolver.ResourceResolver;
import org.junit.Test;

import java.math.MathContext;
import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class EnvironmentConfigurationBuilderTest {

    private final UnaryOperator unaryOperator = mock(UnaryOperator.class);
    private final BinaryOperator binaryOperator = mock(BinaryOperator.class);
    private final AddonParserProvider addonParserProvider = mock(AddonParserProvider.class);

    @Test
    public void parserConfiguration() throws Exception {
        TemplateCacheProvider templateCacheProvider = new NoTemplateCacheProvider();

        EnvironmentConfiguration result = new EnvironmentConfigurationBuilder().parser()
                .syntax()
                .withStartCode("{%").withEndCode("%}")
                .withStartOutput("{{").withEndOutput("}}")
                .withStartComment("{#").withEndComment("#}")
                .and()
                .withAddonParserProvider(customAddonParser())
                .withBinaryOperator(customBinaryOperator())
                .withUnaryOperator(customUnaryOperator())
                .withCacheProvider(templateCacheProvider)
                .and()
                .build();

        JtwigParserConfiguration parser = result.getJtwigParserConfiguration();
        assertThat(parser.getSyntaxConfiguration().getStartCode(), is("{%"));
        assertThat(parser.getSyntaxConfiguration().getEndCode(), is("%}"));
        assertThat(parser.getSyntaxConfiguration().getStartOutput(), is("{{"));
        assertThat(parser.getSyntaxConfiguration().getEndOutput(), is("}}"));
        assertThat(parser.getSyntaxConfiguration().getStartComment(), is("{#"));
        assertThat(parser.getSyntaxConfiguration().getEndComment(), is("#}"));
        assertThat(parser.getAddonParserProviders(), hasItem(addonParserProvider));
        assertThat(parser.getBinaryOperators(), hasItem(binaryOperator));
        assertThat(parser.getUnaryOperators(), hasItem(unaryOperator));
        assertThat(parser.getTemplateCacheProvider(), is(templateCacheProvider));
    }

    @Test
    public void functions() throws Exception {
        JtwigFunction jtwigFunction = mock(JtwigFunction.class);

        EnvironmentConfiguration result = new EnvironmentConfigurationBuilder().functions()
                .withFunction(jtwigFunction)
                .and()
                .build();

        assertThat(result.getFunctionResolverConfiguration().getFunctions(), hasItem(jtwigFunction));
    }

    @Test
    public void resources() throws Exception {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);

        EnvironmentConfiguration result = new EnvironmentConfigurationBuilder().resources()
                .withResourceResolver(resourceResolver)
                .and()
                .build();

        assertThat(result.getResourceResolverConfiguration().getResourceResolvers(), hasItem(resourceResolver));
    }

    @Test
    public void renderConfig() throws Exception {

        EscapeMode initialEscapeMode = EscapeMode.NONE;
        MathContext mathContext = MathContext.DECIMAL128;
        boolean strictMode = false;
        Charset outputCharset = Charset.defaultCharset();

        EnvironmentConfiguration result = new EnvironmentConfigurationBuilder()
                .render()
                .withStrictMode(strictMode)
                .withMathContext(mathContext)
                .withInitialEscapeMode(initialEscapeMode)
                .withOutputCharset(outputCharset)
                .and().build();

        assertThat(result.getRenderConfiguration().initialEscapeMode(), is(initialEscapeMode));
        assertThat(result.getRenderConfiguration().outputCharset(), is(outputCharset));
        assertThat(result.getRenderConfiguration().strictMode(), is(strictMode));
        assertThat(result.getRenderConfiguration().mathContext(), is(mathContext));
    }

    private UnaryOperator customUnaryOperator() {
        return unaryOperator;
    }

    private BinaryOperator customBinaryOperator() {
        return binaryOperator;
    }

    private AddonParserProvider customAddonParser() {
        return addonParserProvider;
    }
}