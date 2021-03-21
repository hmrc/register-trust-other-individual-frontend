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

package mapping.reads

import mapping.register.IdentificationMapper._
import models._
import play.api.libs.json.{Format, Json}

import java.time.LocalDate

final case class OtherIndividual(name: FullName,
                                 dateOfBirth: Option[LocalDate],
                                 nationalInsuranceNumber: Option[String],
                                 ukAddress: Option[UkAddress],
                                 internationalAddress: Option[InternationalAddress],
                                 passportDetails: Option[PassportOrIdCardDetails],
                                 idCardDetails: Option[PassportOrIdCardDetails],
                                 countryOfResidence: Option[String],
                                 countryOfNationality: Option[String],
                                 mentalCapacityYesNo: Option[Boolean]) {

  def address: Option[AddressType] = buildValue(ukAddress, internationalAddress)(buildAddress)

  def identification: Option[IdentificationType] = (nationalInsuranceNumber, address, passportDetails, idCardDetails) match {
    case (None, None, None, None) => None
    case (Some(_), _, _, _) => Some(IdentificationType(nationalInsuranceNumber, None, None))
    case _ => Some(IdentificationType(None, buildValue(passportDetails, idCardDetails)(buildPassport), address))
  }
}

object OtherIndividual {
  implicit val classFormat: Format[OtherIndividual] = Json.format[OtherIndividual]
}
