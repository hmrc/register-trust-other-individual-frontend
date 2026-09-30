/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import base.SpecBase
import play.api.libs.json.{JsSuccess, Json}

class AddressSpec extends SpecBase {

  private val uk   = UkAddress("line1", "line2", None, None, "AB1 1AB")
  private val intl = InternationalAddress("line1", "line2", None, "FR")

  "Address" must {

    "read a UK address" in {
      Json.toJson(uk).validate[Address] mustEqual JsSuccess(uk)
    }

    "read an international address" in {
      Json.toJson(intl).validate[Address] mustEqual JsSuccess(intl)
    }

    "write a UK address" in {
      Json.toJson[Address](uk) mustEqual Json.toJson(uk)
    }

    "write an international address" in {
      Json.toJson[Address](intl) mustEqual Json.toJson(intl)
    }
  }

}
