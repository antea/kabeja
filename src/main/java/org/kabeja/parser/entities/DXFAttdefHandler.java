/*
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.kabeja.parser.entities;

import org.apache.commons.lang.StringUtils;
import org.kabeja.dxf.DXFAttdef;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.parser.DXFValue;

/**
 * @author Federico Russo
 *
 */
public class DXFAttdefHandler extends DXFTextHandler {

    public static final int ATTDEF_TAG = 2;
    public static final int ATTDEF_VERTICAL_ALIGN = 74;
    public static final int ATTDEF_TEXT_LENGTH = 73;

    public DXFAttdefHandler() {
        super();
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#parseGroup(int, de.miethxml.kabeja.parser.DXFValue)
     */
    @Override
    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
            case ATTDEF_TEXT_LENGTH:

                //ignore not used by
                break;

            case ATTDEF_VERTICAL_ALIGN:
                text.setValign(value.getIntegerValue());

                break;

            default:
                super.parseGroup(groupCode, value);
        }

        switch (groupCode) {
            case ATTDEF_TAG:
                if (StringUtils.isBlank(text.getText())) {
                    text.setText(value.getValue());
                    content = value.getValue();
                }
        }
    }

    @Override
    public void startDXFEntity() {
        text = new DXFAttdef();
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#getDXFEntityName()
     */
    @Override
    public String getDXFEntityName() {
        return DXFConstants.ENTITY_TYPE_ATTDEF;
    }
}
