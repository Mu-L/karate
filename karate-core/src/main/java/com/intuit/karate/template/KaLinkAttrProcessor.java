/*
 * The MIT License
 *
 * Copyright 2022 Karate Labs Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate.template;

import com.intuit.karate.http.ServerConfig;
import com.intuit.karate.resource.Resource;
import com.intuit.karate.resource.ResourceResolver;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author pthomas3
 */
public class KaLinkAttrProcessor extends AbstractAttributeTagProcessor {

    private static final Logger logger = LoggerFactory.getLogger(KaLinkAttrProcessor.class);
    
    private static final String HREF = "href";

    private final ResourceResolver resourceResolver;
    private final String hostContextPath;

    public KaLinkAttrProcessor(String dialectPrefix, ServerConfig config) {
        super(TemplateMode.HTML, dialectPrefix, "link", false, HREF, false, 1000, false);
        hostContextPath = config.getHostContextPath();
        resourceResolver = config.getResourceResolver();
    }

    @Override
    protected void doProcess(ITemplateContext ctx, IProcessableElementTag tag, AttributeName an, String av, IElementTagStructureHandler sh) {
        String href = hostContextPath == null ? av : hostContextPath + av;
        String noCache = tag.getAttributeValue(getDialectPrefix(), KaScriptElemProcessor.NOCACHE);
        if (noCache != null) {
            try {
                Resource resource = resourceResolver.resolve(href);
                href = href + "?ts=" + resource.getLastModified();
            } catch (Exception e) {
                logger.warn("nocache failed: {}", e.getMessage());
            }
            sh.removeAttribute(getDialectPrefix(), KaScriptElemProcessor.NOCACHE);
        }
        sh.setAttribute(HREF, href);
    }

}
