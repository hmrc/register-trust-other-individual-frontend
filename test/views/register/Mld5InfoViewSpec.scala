/*
 * Copyright 2021 HM Revenue & Customs
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
import views.html.register.Mld5InfoView

class Mld5InfoViewSpec extends ViewBehaviours {

  "Mld5Info view" must {

    val view = viewFor[Mld5InfoView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPageTitleWithCaption(applyView, "otherIndividualsInfo.5mld",
      "caption",
      "paragraph1",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "paragraph2",
      "details",
      "details.paragraph1"
    )

    behave like pageWithBackLink(applyView)
  }
}
