/*
 * Copyright 2020 HM Revenue & Customs
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

package mapping.register

import java.time.LocalDate

import base.SpecBase
import generators.Generators
import models.{FullName, InternationalAddress, PassportOrIdCardDetails, UkAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.individual._

class IndividualProtectorMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val mapper = injector.instanceOf[IndividualProtectorMapper]
  private val index0 = 0
  private val index1 = 1
  private val firstName = "first"
  private val lastName = "last"
  private val nino = "AB123456C"
  private val dateOfBirth = LocalDate.of(2000,1,1)
  private val passportExpiry = LocalDate.of(2025,1,1)

  "IndividualProtectorMapper" when {

    "when user answers is empty" must {

      "must return None" in {

        val userAnswers = emptyUserAnswers

        mapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "return mapped data" when {

        "nino is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), false).success.value
              .set(NationalInsuranceYesNoPage(index0), true).success.value
              .set(NationalInsuranceNumberPage(index0), nino).success.value

          val individualProtectors = mapper.build(userAnswers)

          individualProtectors mustBe defined
          individualProtectors.value.head mustBe Protector(
            name = NameType(firstName, None, lastName),
            dateOfBirth = None,
            identification = Some(IdentificationType(nino = Some(nino), address = None, passport = None))
          )
        }

        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), false).success.value
              .set(NationalInsuranceYesNoPage(index0), false).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUkYesNoPage(index0), true).success.value
              .set(UkAddressPage(index0), UkAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
              .set(PassportDetailsYesNoPage(index0), false).success.value
              .set(IDCardDetailsYesNoPage(index0), false).success.value

          val individualProtectors = mapper.build(userAnswers)

          individualProtectors mustBe defined
          individualProtectors.value.head mustBe Protector(
            name = NameType(firstName, None, lastName),
            identification = Some(IdentificationType(nino = None,
              address = Some(AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")),
              passport = None
            )),
            dateOfBirth = None
          )
        }

        "International Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), false).success.value
              .set(NationalInsuranceYesNoPage(index0), false).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUkYesNoPage(index0), false).success.value
              .set(NonUkAddressPage(index0), InternationalAddress("Line1", "Line2", Some("Line3"), "US")).success.value
              .set(PassportDetailsYesNoPage(index0), false).success.value
              .set(IDCardDetailsYesNoPage(index0), false).success.value

          val individualProtectors = mapper.build(userAnswers)

          individualProtectors mustBe defined
          individualProtectors.value.head mustBe Protector(
            name = NameType(firstName, None, lastName),
            identification = Some(IdentificationType(
              nino = None,
              address = Some(AddressType("Line1", "Line2", Some("Line3"), None, None, "US")),
              passport = None
            )),
            dateOfBirth = None
          )
        }

        "DateOfBirth, Address and Passport is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), true).success.value
              .set(DateOfBirthPage(index0), dateOfBirth).success.value
              .set(NationalInsuranceYesNoPage(index0), false).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUkYesNoPage(index0), true).success.value
              .set(UkAddressPage(index0), UkAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
              .set(PassportDetailsYesNoPage(index0), true).success.value
              .set(PassportDetailsPage(index0), PassportOrIdCardDetails("GB", "12345", passportExpiry)).success.value
              .set(IDCardDetailsYesNoPage(index0), false).success.value

          val individualProtectors = mapper.build(userAnswers)

          individualProtectors mustBe defined
          individualProtectors.value.head mustBe Protector(
            name = NameType(firstName, None, lastName),
            identification = Some(IdentificationType(nino = None,
              address = Some(AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")),
              passport = Some(PassportType("12345", passportExpiry, "GB"))
            )),
            dateOfBirth = Some(dateOfBirth)
          )
        }
      }

      "must be able to create multiple Individual Protectors" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), FullName("Individual Name 1", None, lastName)).success.value
            .set(DateOfBirthYesNoPage(index0), false).success.value
            .set(NationalInsuranceYesNoPage(index0), true).success.value
            .set(NationalInsuranceNumberPage(index0), nino).success.value

            .set(NamePage(index1), FullName("Individual Name 2", None, lastName)).success.value
            .set(DateOfBirthYesNoPage(index1), false).success.value
            .set(NationalInsuranceYesNoPage(index1), false).success.value
            .set(AddressYesNoPage(index1), true).success.value
            .set(AddressUkYesNoPage(index1), true).success.value
            .set(UkAddressPage(index1),
              UkAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
            .set(PassportDetailsYesNoPage(index1), false).success.value
            .set(IDCardDetailsYesNoPage(index1), false).success.value


        val individualProtectors = mapper.build(userAnswers)

        individualProtectors mustBe defined
        individualProtectors.value mustBe
          List(
            Protector(
              name = NameType("Individual Name 1", None, lastName),
              dateOfBirth = None,
              identification = Some(IdentificationType(nino = Some(nino), address = None, passport = None))),

            Protector(
              name = NameType("Individual Name 2", None, lastName),
              dateOfBirth = None,
              identification = Some(
                IdentificationType(
                  nino = None,
                  address = Some(AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")),
                  passport = None
                )
              ))
          )
      }

    }
  }
}

