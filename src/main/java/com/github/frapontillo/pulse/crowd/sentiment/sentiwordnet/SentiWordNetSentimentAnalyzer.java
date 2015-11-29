/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.frapontillo.pulse.crowd.sentiment.sentiwordnet;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.frapontillo.pulse.crowd.sentiment.ISentimentAnalyzerOperator;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.spi.VoidConfig;
import com.github.frapontillo.pulse.util.StringUtil;
import rx.Observable;

/**
 * Sentiment Analyzer based on MultiWordNet and SentiWordNet.
 * Each message is processed as follows:
 * <p/>
 * <ol>
 * <li>Tokens are extracted.</li>
 * <li>Each token has a lemma, a language and a generic simple POS tag ("n", "v", "a", "r"), so a
 * collection of synsets is extracted from MultiWordNet according to those token features.</li>
 * <li>For every synsets, sentiment scores are extracted from SentiWordNet (language-independent)
 * and an average is calculated on those scores.</li>
 * <li>When the sentiment analysis is done for every token, a sentiment average is computed on the
 * whole token list.</li>
 * </ol>
 * <p/>
 * TODO: this algorithm can be improved in multiple ways:
 * TODO: abstract logic of sentiment computation for each Token (multiple synsets/sentiments)
 * TODO: abstract logic of sentiment computation for each Message (average of Token sentiment)
 * TODO: add configurations to the analyzer to weight some simple POS tags more than others
 *
 * @author Francesco Pontillo
 */
public class SentiWordNetSentimentAnalyzer extends IPlugin<Message, Message, VoidConfig> {
    public final static String PLUGIN_NAME = "sentiwordnet";
    private final MultiWordNet multiWordNet;
    private final SentiWordNet sentiWordNet;

    public SentiWordNetSentimentAnalyzer() {
        multiWordNet = new MultiWordNet();
        sentiWordNet = new SentiWordNet();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override protected Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return new ISentimentAnalyzerOperator(this) {
            @Override public Message sentimentAnalyze(Message message) {
                double totalScore = 0;
                double lemmatizedTokens = 0;
                if (message.getTokens() == null) {
                    return message;
                }
                for (Token token : message.getTokens()) {
                    if (!StringUtil.isNullOrEmpty(token.getLemma())) {
                        // retrieve and optionally filter the synsets according to WordNet POS tags
                        String[] synsets = multiWordNet
                                .getSynsets(token.getLemma(), message.getLanguage(),
                                        token.getSimplePos());
                        // TODO: add weights for simple POS according to some specific configuration
                        double synsetScore = sentiWordNet.getScore(synsets);
                        totalScore += synsetScore;
                        lemmatizedTokens += 1;
                        token.setScore(synsetScore);
                    }
                }
                if (lemmatizedTokens > 0) {
                    message.setSentiment(totalScore / lemmatizedTokens);
                } else {
                    message.setSentiment(0);
                }
                return message;
            }
        };
    }

}
