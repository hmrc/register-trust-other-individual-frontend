/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.register

import views.behaviours.ViewBehaviours
import views.html.register.InfoView

class InfoViewSpec extends ViewBehaviours {

  "Mld5Info view" when {

    val view = viewFor[InfoView](Some(emptyUserAnswers))

    "taxable" must {

      val applyView = view.apply(fakeDraftId, isTaxable = true)(fakeRequest, messages)

      behave like normalPageTitleWithSectionSubheading(applyView, "otherIndividualsInfo.5mld",
        "paragraph1",
        "bulletpoint1",
        "bulletpoint2",
        "bulletpoint3",
        "paragraph2",
        "bulletpoint4",
        "bulletpoint5",
        "bulletpoint6",
        "bulletpoint7",
        "paragraph3",
        "paragraph4",
        "bulletpoint8",
        "bulletpoint9",
        "bulletpoint10",
        "bulletpoint11",
        "details",
        "details.paragraph1"
      )

      behave like pageWithBackLink(applyView)
    }

    "non-taxable" must {

      val applyView = view.apply(fakeDraftId, isTaxable = false)(fakeRequest, messages)

      behave like normalPageTitleWithSectionSubheading(applyView, "otherIndividualsInfo.5mld",
        "paragraph1",
        "bulletpoint1",
        "bulletpoint2",
        "bulletpoint3",
        "paragraph3",
        "paragraph4",
        "bulletpoint8",
        "bulletpoint9",
        "bulletpoint10",
        "bulletpoint11",
        "details",
        "details.paragraph1"
      )

      behave like pageWithBackLink(applyView)
    }
  }
}
